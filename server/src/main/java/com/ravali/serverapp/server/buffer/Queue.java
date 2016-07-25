package com.ravali.serverapp.server.buffer;

import java.util.ArrayList;
import java.util.List;

import com.ravali.serverapp.server.data.Message;

public class Queue {
	
	private static final int DEFAULT_CAPACITY = 1024;

	private List<Message> queue;
	private int capacity;

	public Queue() {
		queue = new ArrayList<Message>(DEFAULT_CAPACITY);
		capacity = DEFAULT_CAPACITY;
	}

	public Queue(int capacity) {
		queue = new ArrayList<Message>(DEFAULT_CAPACITY);
		this.capacity = capacity;
	}

	public void enqueue(Message data) {
		queue.add(data);
	}

	public Message dequeue() {
		Message message = queue.remove(0);
		return message;
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public boolean isFull() {
		return (queue.size() == capacity);

	}


}
