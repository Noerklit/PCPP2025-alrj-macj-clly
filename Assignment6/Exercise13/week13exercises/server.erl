% raup@itu.dk * 2024-11-22

-module(server).
-export([start/1, start/2, init/2]). % to be extended
-include("defs.hrl").

% 1. State
-record(serverState, {idle_workers, busy_workers = [], pending_tasks = [], minWorkers = 0, maxWorkers}).
%-record(worker_task, {senderPID, task}).
% 2. Start
start(Number_workers) ->
    start(Number_workers, Number_workers).
start(MinWorkers, MaxWorkers) ->
    spawn(?MODULE, init, [MinWorkers, MaxWorkers]).

% 3. Initialization
init(MinWorkers, MaxWorkers) ->
    % Spawn our fixed amount of workers, based on lecture code?
    Workers = lists:map(fun (_) -> worker:start(self()) end,
                            lists:seq(0, MinWorkers-1)),
    InitialState = #serverState{idle_workers = Workers, busy_workers = [],pending_tasks = [], minWorkers = MinWorkers, maxWorkers = MaxWorkers},
    loop(InitialState).


% 4. Behavior upon receiving messages
loop(State) -> 
    print_workers(State),
    receive
        {compute, SenderPID, Tasks} ->
            TasksWithSender = lists:map(fun (Task) -> {compute, SenderPID, Task} end, Tasks),
            loop(handle_tasks(State, TasksWithSender));
        {work_done, WorkerPID} ->
            IdleWorkers = State#serverState.idle_workers,
            BusyWorkers = State#serverState.busy_workers,
            NewState = State#serverState{idle_workers = [WorkerPID | IdleWorkers], busy_workers = lists:delete(WorkerPID, BusyWorkers)},
            loop(handle_tasks(eliminate_workers(NewState),[]));
        _ -> 
          loop(State)
    end.

% 5. Message handlers

% Private functions (to be used, e.g., by message handlers)
%
print_workers(#serverState{idle_workers = IdleWorkers, busy_workers = BusyWorkers}) ->
  io:format("Number of workers: ~p~n", [erlang:length(IdleWorkers++BusyWorkers)]).

any_tasks_left(#serverState{pending_tasks = PendingTasks}) ->
  erlang:length(PendingTasks) > 0.

eliminate_workers(State = #serverState{idle_workers = []}) ->
    State;
eliminate_workers(State = #serverState{idle_workers = [IdleWorker | IdleWorkers], busy_workers = BusyWorkers, minWorkers = MinWorkers}) ->
  NWorkers = erlang:length(IdleWorkers ++ BusyWorkers) + 1,
  HasPendingTasks = any_tasks_left(State),
  if 
    NWorkers > MinWorkers andalso not HasPendingTasks ->
      IdleWorker ! stop,
      eliminate_workers(State#serverState{idle_workers = IdleWorkers});
    true ->
      State
  end.

spawn_worker(
  State = #serverState{
             idle_workers = IdleWorkers}) ->
    State#serverState{idle_workers = [worker:start(self()) | IdleWorkers]}.

handle_tasks(
    State = #serverState{
        idle_workers = [],
        maxWorkers = MaxWorkers,
        busy_workers = BusyWorkers},
    Tasks) ->
    if 
        erlang:length(BusyWorkers) < MaxWorkers ->
           handle_tasks(spawn_worker(State), Tasks);
        true -> 
            State#serverState{pending_tasks = State#serverState.pending_tasks ++ Tasks}
    end;

handle_tasks(
    State = #serverState{
        idle_workers = [Worker | Workers],
        busy_workers = BusyWorkers,
        pending_tasks = []},
    [Task | Tasks]) ->
    Worker ! Task,
    handle_tasks(State#serverState{idle_workers = Workers, busy_workers = [Worker | BusyWorkers]}, Tasks);

handle_tasks(
    State = #serverState{
        idle_workers = [Worker | Workers],
        busy_workers = BusyWorkers,
        pending_tasks = [Task | PendingTasks]},
    Tasks) ->
    Worker ! Task,
    handle_tasks(State#serverState{
        idle_workers = Workers,
        pending_tasks = PendingTasks,
        busy_workers = [Worker | BusyWorkers]},
        Tasks);

handle_tasks(
    State,
    _) ->
    State.

