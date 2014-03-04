/**
 * 
 */
package com.thisisnoble.javatest.impl;

import java.util.ArrayList;
import java.util.List;

import com.thisisnoble.javatest.Event;
import com.thisisnoble.javatest.Orchestrator;
import com.thisisnoble.javatest.Processor;
import com.thisisnoble.javatest.Publisher;

/**
 * @author Pratik
 * 
 */
public class OrchestratorImpl implements Orchestrator {
	
	
	Publisher publisher;
	
	List<Processor> processors = new ArrayList<Processor>();
	
	CompositeEvent compositeEvent = null;
	
	String parentId = null;

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thisisnoble.javatest.Orchestrator#register(com.thisisnoble.javatest
	 *      .Processor)
	 */
	@Override
	public void register(Processor processor) {
		this.processors.add(processor);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thisisnoble.javatest.Orchestrator#receive(com.thisisnoble.javatest.Event)
	 */
	@Override
	public void receive(Event event) {

		//processed events
		if (compositeEvent != null && compositeEvent.getId().equals(parentId)) {
			compositeEvent.addChild(event);
		} 
		// First event
		else {
			for (Processor processor : this.processors) {
				
				//parent event
				if (processor.interestedIn(event) && compositeEvent == null) {
					processor.process(event);
					compositeEvent = new CompositeEvent(event);
					this.parentId = compositeEvent.getId();
				} 
				//other events
				else {
					processor.process(event);
				}
			}
		}

		this.publisher.publish(compositeEvent);

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.thisisnoble.javatest.Orchestrator#setup(com.thisisnoble.javatest.
	 *      Publisher)
	 */
	@Override
	public void setup(Publisher publisher) {
		this.publisher = publisher;
	}

}
