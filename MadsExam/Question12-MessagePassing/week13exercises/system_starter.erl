
-module(system_starter).
-compile(export_all).

init_systems() ->
    Server = server:start(10),
    client:start(Server).
