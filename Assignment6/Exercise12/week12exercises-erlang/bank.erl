-module(bank).

-export([start/0, init/0]).
-record(transaction, {from, to, amount}).

start() ->
    spawn(?MODULE, init, []).

init() ->
    loop().

transfer(FROM, TO, Amount) ->
    FROM ! {deposit, -Amount},
    TO ! {deposit, Amount}.

loop() ->
    receive
        {transaction, Trans} -> 
            Account1 = Trans#transaction.from,
            Account2 = Trans#transaction.to,
            Amount = Trans#transaction.amount,
            transfer(Account1, Account2, Amount),
            loop();
        _ -> loop()
    end.

