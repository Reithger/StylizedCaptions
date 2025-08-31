package control;

import java.util.ArrayList;

import core.JavaReceiver;
import core.JavaSender;
import core.SocketControl;
import localside.MessageSender;

public class ExportHandler implements JavaReceiver, JavaSender, TextReceiver{

	private MessageSender send;
	private SocketControl controller;
	private String terminateName;
	private String sendSocketTitle;
	
	private String lastMessage;
	
	public ExportHandler(SocketControl socket, String sendSocket, String in) {
		controller = socket;
		sendSocketTitle = sendSocket;
		terminateName = in;
	}
	
	@Override
	public void handleText(String in) {
		if(send != null && !in.equals(lastMessage)) {
			try {
				send.sendMessage(sendSocketTitle, in);
				lastMessage = in;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(send == null){
			System.err.println("Sender is null and cannot send message: " + in + " via Sender: " + sendSocketTitle);
		}
	}

	@Override
	public void receiveMessageSender(MessageSender sender) {
		send = sender;
	}

	@Override
	public void receiveSocketData(String socketData, ArrayList<String> tags) {
		if(socketData.toLowerCase().equals("end")) {
			controller.endSocketInstance(terminateName);
		}
	}

}
