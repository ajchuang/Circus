Circus
======

A Circuit Simulator for SDN final project
The goal is to implement a optical circuit switch simulator for testing
the circuit language.

DESIGN SPEC
======
- For each switch
    - Basics
        - TCP connection to the controller
        - listen to a UDP port for data packets
        - forward data in the UDP port
        - maintain a list of outgoing port and incoming port
            - switch port: (ip:port)
    
    - SDN related
        - a table of existing flows (configured by controller)
            - in port : out port : lambda_in : lambda_out : time_slot 