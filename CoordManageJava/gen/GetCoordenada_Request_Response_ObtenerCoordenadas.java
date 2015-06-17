package gen;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class GetCoordenada_Request_Response_ObtenerCoordenadas extends
		MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		try {
				// create new message as a copy of the input
				MbMessage outMessage = new MbMessage(inMessage);
				outAssembly = new MbMessageAssembly(inAssembly, outMessage);
				// ----------------------------------------------------------
				// Add user code below
				MbElement rootElement = outMessage.getRootElement();
				
				//****************************Borrar los datos del mensaje de entrada****************************
				rootElement.getFirstElementByPath("/XMLNSC/getCoordenada").delete();
				
				//**********************************Coneccion con la base de datos*******************************
				Class.forName("COM.ibm.db2os390.sqlj.jdbc.DB2SQLJDriver");
				Connection connection = 
						DriverManager.getConnection("jdbc:db2://172.16.11.225:50000/IIBDB", "admin","Thisli07");
				
				//*****************************Preparar query a la base de datos********************************
				String query = "Select \"latitud\",\"longitud\",\"descripcion\" from \"Coordenada\"";
				CallableStatement cStmt = connection.prepareCall(query);
		        
		        //*****************************Ejecutar query a la base de datos********************************
				cStmt.execute();

		        ResultSet rs = cStmt.getResultSet();
				

				MbElement DataElement = rootElement.getFirstElementByPath("/XMLNSC");
				DataElement.createElementAsFirstChild(MbElement.TYPE_NAME,"getCoordenadaResponse", "");
				
				//****************************Procesar resultado*************************************************
				
				while(rs.next()){
					String latitud = rs.getString("latitud");
					String longitud = rs.getString("longitud");
					String descripcion = rs.getString("descripcion");
					DataElement = rootElement.getFirstElementByPath("/XMLNSC/getCoordenadaResponse");
					DataElement.createElementAsFirstChild(MbElement.TYPE_NAME,"resultado", "");
					DataElement =  rootElement.getFirstElementByPath("/XMLNSC/getCoordenadaResponse/resultado");
					DataElement.createElementAsLastChild(MbElement.TYPE_NAME, "latitud", latitud);
					DataElement.createElementAsLastChild(MbElement.TYPE_NAME, "longitud",longitud);
					DataElement.createElementAsLastChild(MbElement.TYPE_NAME, "descripcion", descripcion);
				}
				
			
			// End of user code
			// ----------------------------------------------------------

		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
			
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
