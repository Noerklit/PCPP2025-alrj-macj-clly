-module(mobile_app).
-include("records.hrl").

-export([start/1, init/1]).

-record(transaction, {from, to, amount}).
-record(mobile_app_state, {bank}).

start(BANK) ->
    InitialState = #mobile_app_state{bank = BANK},
    spawn(?MODULE, init, [InitialState]).

init(InitialState) ->
    loop(InitialState).

loop(State) ->
    receive
        {payment_request, Request} -> 
            From = Request#payment_request.from,
            To = Request#payment_request.to,
            Amount = Request#payment_request.amount,
            State#mobile_app_state.bank ! {transaction, #transaction{from = From, to = To, amount = Amount}};
        _ -> loop(State)
    end.

