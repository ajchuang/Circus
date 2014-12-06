package nonstar.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import nonstar.network.NetworkEnv;
import CircusCommunication.CircusComm;
import CircusCommunication.CircusCommConst;
import CircusCommunication.CircusCommObj;

// cmd format: {cs_insert swId dstSw srcSw inlambda outlambda tdm_id}

//cmd format: {ps_insert swId dir srcIp dstIp out/in_Sw lambda tdm_id}
//dir can be cp or pc mean flow is from C2P or P2C

public class Controller {

	int m_dbgPort;
	int m_ctlPort;
	ConcurrentHashMap<Integer, ObjectOutputStream> m_output;
	NetworkEnv netEnv;


	public static void log (String s) {
		System.out.println ("[Controller] " + s);
	}

	public Controller (int cport, int dport) {
		m_dbgPort = dport;
		m_ctlPort = cport;
		m_output = new ConcurrentHashMap<Integer, ObjectOutputStream> ();
		netEnv = new NetworkEnv();

		log ("Controller: " + m_ctlPort + ":" + m_dbgPort);
	}

	public void startService () {
		new Thread (new CPlaneServ (m_ctlPort, this)).start ();
		new Thread (new DebugServer (m_dbgPort, this)).start ();
	}

	public static void main (String args[]) {

		if (args.length != 2) {
			log ("2 params are necessary");
			return;
		}

		int cPort = Integer.parseInt (args[0]);
		int dPort = Integer.parseInt (args[1]);

		Controller ctc = new Controller (cPort, dPort);
		ctc.startService ();
	}

	void startCPlaneServ (Socket s, NetworkEnv env) {
		new Thread (new CPlaneSwitchServ (s, env)).start ();
	}

	private class CPlaneSwitchServ implements Runnable {

		Socket m_sk;
		ObjectInputStream   m_ois;
		ObjectOutputStream  m_oos;
		NetworkEnv nEnv;

		public CPlaneSwitchServ (Socket skt, NetworkEnv env) {
			m_sk = skt;
			nEnv = env;
		}

		@Override
		public void run () {

			try {
				m_oos = new ObjectOutputStream (m_sk.getOutputStream ());
				m_ois = new ObjectInputStream (m_sk.getInputStream ());
			} catch (Exception e) {
				log ("WTF: " + e);
				return;
			}

			while (true) {
				try {
					log ("Starting to wait object");
					Object obj = m_ois.readObject ();

					if (!(obj instanceof CircusCommObj)) {
						log ("bad type");
						continue;
					}

					CircusCommObj cco = (CircusCommObj) obj;

					/* process the cco */
					int msg = cco.getMsgType ();
					log ("Receiving " + msg + " from " + cco.getSender ());

					/* handle syson message */
					if (msg == CircusCommConst.mtype_sysup) {

						log ("Processing sysup from " + cco.getSender ());
						Integer ikey = Integer.valueOf (cco.getSender ());
						m_output.put (ikey, m_oos);
						nEnv.addSwitch(cco, m_oos);
					} else if (msg == CircusCommConst.mtype_sysdown) {

						log ("Processing sysdown from " + cco.getSender ());
						Integer ikey = Integer.valueOf (cco.getSender ());
						m_output.remove (ikey, m_oos);

						/* terminate this thread */
						return;
					} else if (msg == CircusCommConst.mtype_ack) {
						log("Processing ack from " + cco.getSender());
					} else if (msg == CircusCommConst.mtype_nack) {
						log("Processing nack from " + cco.getSender());
					} else {
						nEnv.processCommObj(cco);
					}

				} catch (Exception e) {
					log ("Ooops: " + e);
					e.printStackTrace ();
				}
			}
		}
	}

	private class CPlaneServ implements Runnable {

		int m_cpPort;
		Controller m_me;
		ServerSocket ss;

		public CPlaneServ (int port, Controller me) {
			m_cpPort = port;
			m_me = me;
		}

		/* it's just a server thread used to accept the incoming connection */
		@Override
		public void run () {

			try {
				/* welcome sockets to listen to the incoming sockets */
				ss = new ServerSocket (m_cpPort);

				while (true) {
					Socket sc = ss.accept ();
					log ("Incoming connection");

					m_me.startCPlaneServ (sc, m_me.netEnv);
				}
			} catch (Exception e) {
				log ("Ooops: " + e);
				e.printStackTrace ();
			}
		}
	}

	private class DebugServer implements Runnable {

		int m_dbgPort;
		Controller m_me;
		ServerSocket ss;

		public DebugServer (int dport, Controller me) {
			m_dbgPort = dport;
			m_me = me;
		}

		void procCsIns (String cmd, PrintStream out) {
			// cmd format: {cs_insert swId dstSw srcSw inlambda outlambda tdm_id}
			//  txSetup_cs (int dstSw, int srcSw, int inlambda, int outlambda, int tdm_id, ObjectOutputStream oos) {

			out.println ("Processing CS insert...");

			String toks[] = cmd.split ("\\s+");

			if (toks.length != 7) {
				out.println ("format error");
				return;
			}

			int swId = Integer.parseInt (toks[1]);
			int dstSw = Integer.parseInt (toks[2]);
			int srcSw = Integer.parseInt (toks[3]);
			int inlambda = Integer.parseInt (toks[4]);
			int outlambda = Integer.parseInt (toks[5]);
			int tdmid = Integer.parseInt (toks[6]);

			Integer iid = Integer.valueOf (swId);
			ObjectOutputStream oos = m_output.get (iid);

			if (oos == null) {
				out.println ("ERROR: sw id " + swId + " is not found");
				return;
			}

			/* send setup message */
			if (CircusComm.txSetup_cs (dstSw,srcSw, inlambda,outlambda, tdmid, oos))
				out.println ("OK");
			else
				out.println ("ERROR: Cplane failed");
		}

		void procPsIns (String cmd, PrintStream out) {
			// cmd format: {PS_insert swId dir srcIp dstIp out/in_Sw lambda tdm_id}
			//dir can be CP or PC mean flow is from C2P or P2C
			//txAddEntry_ps_C2P (String srcIp, String dstIp, int inSw, int lambda, int tdm_id, ObjectOutputStream oos)
			out.println ("Processing PS insert...");

			String toks[] = cmd.split ("\\s+");

			if (toks.length != 8) {
				out.println ("format error");
				return;
			}

			int swId    = Integer.parseInt (toks[1]);
			String dir = toks[2];
			String srcIp  = toks[3];
			String dstIp   = toks[4];
			int inoutSw   = Integer.parseInt (toks[5]);
			int lambda = Integer.parseInt (toks[6]);
			int tdmId = Integer.parseInt (toks[6]);

			Integer iid = Integer.valueOf (swId);
			ObjectOutputStream oos = m_output.get (iid);

			if (oos == null) {
				out.println ("ERROR: sw id " + swId + " is not found");
				return;
			}

			if(dir.equals("cp")){
				if (CircusComm.txAddEntry_ps_C2P ( srcIp,  dstIp,  inoutSw,  lambda,  tdmId,  oos) )
					out.println ("OK");
				else
					out.println ("ERROR: fail to write");

				return;
			}

			else if(dir.equals("pc")){
				if (CircusComm.txAddEntry_ps_P2C ( srcIp,  dstIp,  inoutSw,  lambda,  tdmId,  oos))
					out.println ("OK");
				else
					out.println ("ERROR: fail to write");

				return;
			}
		}

		void addHost(String cmd, PrintStream out) {
			String toks[] = cmd.split(" +");

			if (toks.length < 3) {
				out.println("Invalid arguments");
				return;
			}

			m_me.netEnv.addHost(Integer.valueOf(toks[1]), toks[2]);
		}

		void connectHost(String cmd, PrintStream out) {
			String toks[] = cmd.split(" +");
			if (toks.length < 3) {
				out.println("Invalid arguments");
				return;
			}

			m_me.netEnv.setupCircuit(toks[1], toks[2]);
		}

		void disconnectHost(String cmd, PrintStream out) {
			String toks[] = cmd.split(" +");

			if (toks.length < 3) {
				out.println("Invalid arguments");
				return;
			}

			m_me.netEnv.tearCircuit(toks[1], toks[2]);
		}

		/* handle the command */
		void procCmd (String cmd, PrintStream out) {

			if (cmd.startsWith ("cs_insert")) {
				procCsIns (cmd, out);
			} else if (cmd.startsWith ("ps_insert")) {
				procPsIns (cmd, out);
			} else if (cmd.startsWith("add_host")) {
				addHost(cmd, out);
			} else if (cmd.startsWith("h_connect")) {
				connectHost(cmd, out);
			} else if (cmd.startsWith("h_disconnect")) {
				disconnectHost(cmd, out);
			}
		}

		@Override
		public void run () {

			log ("Debug Server @ SW " + m_dbgPort + " is on");

			try {
				/* create the debug server */
				ss = new ServerSocket (m_dbgPort);

				while (true) {

					/* waiting for client */
					Socket sc = ss.accept ();

					/* lots of cliches */
					InputStream is = sc.getInputStream ();
					OutputStream os = sc.getOutputStream ();
					BufferedReader br = new BufferedReader (new InputStreamReader (is));
					PrintStream out = new PrintStream (os);

					/* the string ref for command input */
					String cmd;

					while ((cmd = br.readLine ()) != null) {

						cmd.toLowerCase ();

						/* check if leaving debug */
						if (cmd.equals ("cmd quit"))
							break;

						/* parsing commands, and do something here */
						log (cmd);

						/* TODO */
						procCmd (cmd, out);
					}

					/* clean up */
					out.close ();
					br.close ();
					os.close ();
					is.close ();
					sc.close ();
				}

			} catch (Exception e) {
				log ("Ooops: " + e);
				e.printStackTrace ();
			}
		}
	}

}
