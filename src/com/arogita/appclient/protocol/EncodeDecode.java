package com.arogita.appclient.protocol;



public class EncodeDecode {
	

	public static byte[] encode(Request req){
		byte[] command = req.getCommand();
		// Command length
		int length = command.length;
		
		// Header(2 bytes) + size(1) + command(var) + sum(1)
		int size = 2 + 1 + length + 1;
		byte []  buffer = new byte[size];

		// Put the header
		buffer[0] = 0x55;buffer[1] = (byte)0xAA;
		
		// Fill the length (N+2)
		Integer  N = Integer.valueOf(length+2);
		buffer[2] = (byte) (length +2);
		
		// Fill the command
		for (int i = 0; i < length; i++){
			buffer[i+3] = (byte)command[i];
		}
		
		// Fill the sum
		buffer[size-1] = calcualteNegatedSum(command);
		
		//dumpBuffer(buffer);
		//System.out.println(byteArrayToHexString(buffer));
		
		return buffer;
	}
	
	public void dumpBuffer(byte[] buffer){
		System.out.println(">>>>Buffer length is "+buffer.length);
		for(int i = 0;i<buffer.length;i++){
			System.out.println(String.format("in hex %x", buffer[i]));
		}
		System.out.println("<<<<<");
	}
	public static String byteArrayToHexString(byte[] b) {
	    StringBuffer sb = new StringBuffer(b.length * 2);
	    for (int i = 0; i < b.length; i++) {
	      int v = b[i] & 0xff;
	      if (v < 16) {
	        sb.append('0');
	      }
	      sb.append(Integer.toHexString(v));
	    }
	    return sb.toString().toUpperCase();
	  }
	
	// Method to compute the complement sum
	public static byte calcualteNegatedSum(byte[] command){
		
		// 2 for (request length + sum) N = n+2
		//int high = ((command[1] & 0xFF00) >> 8);
		int high = (command[1] & 0x00FF);
		int low = (command[0] & 0x00FF);
		Integer sum = 2 + command.length + high + low;
		byte ret =  (byte) ~(sum.byteValue());
		//System.out.println(String.format("in hex %x", ret)+", sum is "+ sum);
		return ret;
		
	}
	

	public static Response decode(byte[] in ){
		if((in[0] == 0x55) && (in[1] == 0xAA)){
			int size = in[2];
			byte[] message = new byte[size-2];
			for (int i = 0 ; i < size-2;i++){
				message[i] = in[3];
			}
			byte complementSum = in[size-1];
			byte sum = calcualteNegatedSum(message);
			
			if (sum != complementSum){				
				System.out.println("ComplementSum didn't matched");
			}
			
			Response.messageType(in);
			
			//return new Response(message);
		}
		else{
			return null;
		}
		return null;
	}


	
	
}
