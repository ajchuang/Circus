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
            
CONFIG FILE (to discuss)
======
/* controller config */
CONTROLLER_IP TCP_PORT NUM_SWITCHES

/* switch map */
SWITCH_ID IP_ADDR UDP_PORT
...

/* switch connection config */
SWITCH_ID PORT_CNT PORT_CONN_1(SWITCH_ID) PORT_CONN_2 ...
...            