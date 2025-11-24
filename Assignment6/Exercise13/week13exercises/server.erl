% raup@itu.dk * 2024-11-22

-module(server).
-export([start/1, init/1]). % to be extended
-include("defs.hrl").

% 1. State
-record(serverState, {idle_workers, busy_workers = [], pending_tasks = []}).

% 2. Start
start(Number_workers) ->
    spawn(?MODULE, init, [Number_workers]).

% 3. Initialization
init(Number_workers) ->
    % Spawn our fixed amount of workers, based on lecture code?
    Workers = lists:map(fun (X) -> {X, worker:start(self())} end,
                            lists:seq(0, Number_workers-1)),
    InitialState = #serverState{idle_workers = maps:from_list(Workers), busy_workers = [],pending_tasks = []},
    loop(InitialState).


% 4. Behavior upon receiving messages
loop(State) -> 
    receive
        {}

% 5. Message handlers

% Private functions (to be used, e.g., by message handlers)

