-module(account).
-export([start/1, init/2]).
-record(account, {balance, accountId}).

start(AccountId) ->
    spawn(?MODULE, init, [0, AccountId]).

init(InitialValue, AccountId) ->
    InitialState = #account{balance = InitialValue, accountId = AccountId},
    loop(InitialState).

loop(State) ->
    receive

        {deposit, Amount} -> 
            CurrentBalance = State#account.balance,
            NewState = State#account{balance = CurrentBalance + Amount},
            %io:format("Hit with balance: ~p~n", [NewState#account.balance]),
            loop(NewState);
        print_balance -> 
            io:format("Account with id ~p has balance ~p~n", [State#account.accountId, State#account.balance]),
            loop(State);
        _ -> loop(State)
    end.
