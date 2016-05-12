package com.cyber.activemq.subpub;

import java.text.DecimalFormat;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * ���պʹ�����Ϣ�ķ��������֣���Ϊͬ�����첽�ģ�
 * һ��ͬ���ķ�ʽ������ͨ��MessageConsumer.receive()������������յ�����Ϣ��
 * ���첽�ķ�������ͨ��ע��һ��MessageListener�ķ���
 */
public class Consumer {
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;

	public Consumer() throws JMSException {
		connectionFactory = new ActiveMQConnectionFactory(
				"tcp://localhost:61616");
		connection = connectionFactory.createConnection();
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	public static void main(String[] args) throws JMSException {
		Consumer consumer = new Consumer();
		args = new String[] { "aaa", "bbb"};
		for (String stock : args) {
			Destination destination = consumer.getSession().createTopic(
					"STOCKS." + stock);
			MessageConsumer messageConsumer = consumer.getSession()
					.createConsumer(destination);
			messageConsumer.setMessageListener(new Listener());
		}
	}

	public Session getSession() {
		return session;
	}
}

class Listener implements MessageListener {

	public void onMessage(Message message) {
		try {
			MapMessage map = (MapMessage) message;
			String stock = map.getString("stock");
			double price = map.getDouble("price");
			double offer = map.getDouble("offer");
			boolean up = map.getBoolean("up");
			DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
			System.out.println(stock + "\t" + df.format(price) + "\t"
					+ df.format(offer) + "\t" + (up ? "up" : "down"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
