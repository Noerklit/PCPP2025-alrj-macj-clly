-module(system_starter).
-include("records.hrl").
-export([system_start2/0]).


system_start2() ->
    A1 = account:start(1),
    A2 = account:start(2),
    B1 = bank:start(),
    B2 = bank:start(),
    MA1 = mobile_app:start(B1),
    MA2 = mobile_app:start(B2),
    make_request(A1, A2, MA1, MA2).

make_request(A1, A2, MA1, MA2) ->
  Payment1 = #payment_request{from = A1, to = A2, amount = 100},
  Payment2 = #payment_request{from = A1, to = A2, amount = -100},
  MA1 ! {payment_request, Payment1},
  MA2 ! {payment_request, Payment2}.

