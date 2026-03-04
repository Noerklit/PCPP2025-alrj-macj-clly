-module(system_utilities).
-include("records.hrl").
-export([init_systems/0, send_requests/1]).

-record(test_system, {a1, a2, b}).

init_systems() ->
    A1 = account:start(1),
    A2 = account:start(2),
    B = bank:start(),
    #test_system{a1 = A1, a2 = A2, b = B}.

send_requests(Test_system) ->
    Bank = Test_system#test_system.b,
    Account1 = Test_system#test_system.a1,
    Account2 = Test_system#test_system.a2,
    mobile_app:send_N_requests(100, Bank, Account1, Account2),
    Account1 ! print_balance,
    Account2 ! print_balance.
