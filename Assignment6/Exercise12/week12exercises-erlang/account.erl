-module(account).
-export([start/0, init/1]).
-record(account, {balance}).

start() ->
    spawn(?MODULE, init, [0]).

init(InitialValue) ->
    InitialState = #account{balance = InitialValue},
    loop(InitialState).

loop(State) ->
    receive
        {deposit, Amount} -> 
            CurrentBalance = State#account.balance,
            NewState = State#account{balance = CurrentBalance + Amount},
            io:format("Hit with balance: ~p~n", [NewState#account.balance]),
            loop(NewState);
        _ -> loop(State)
    end.
