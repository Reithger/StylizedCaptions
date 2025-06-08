package control;

import core.JavaReceiver;
import core.JavaSender;
import core.SocketControl;
import localside.MessageSender;

public class ExportHandler implements JavaReceiver, JavaSender, TextReceiver{

	private MessageSender send;
	private SocketControl controller;
	private String terminateName;
	
	private String lastMessage;
	
	public ExportHandler(SocketControl socket, String in) {
		controller = socket;
		terminateName = in;
	}
	
	@Override
	public void handleText(String in) {
		if(send != null && !in.equals(lastMessage)) {
			try {
				send.sendMessage(in);
				lastMessage = in;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(send == null){
			System.err.println("Sender is null and cannot send message: " + in);
		}
	}

	@Override
	public void receiveMessageSender(MessageSender sender) {
		send = sender;
	}

	@Override
	public void receiveSocketData(String socketData) {
		if(socketData.toLowerCase().equals("end")) {
			controller.endSocketInstance(terminateName);
		}
	}

}
