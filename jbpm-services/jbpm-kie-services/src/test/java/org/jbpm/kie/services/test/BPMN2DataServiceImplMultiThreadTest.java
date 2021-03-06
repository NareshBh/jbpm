package org.jbpm.kie.services.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.util.IoUtils;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BPMN2DataServiceImplMultiThreadTest extends AbstractBaseTest {

	@Before
	public void prepare() {
		configureServices();
	}

	@After
	public void cleanup() {

		cleanupSingletonSessionId();
		close();
	}

	@Test
	public void testBuildProcessDefinitionConcurrent() throws Exception {
		
		final List<ProcessDefinition> defs = new ArrayList<ProcessDefinition>();
		
		byte[] process1 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/repo/processes/general/customtask.bpmn"));
		byte[] process2 = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/repo/processes/general/humanTask.bpmn"));

		final String process1Content = new String(process1, "UTF-8");
		final String process2Content = new String(process2, "UTF-8");
		
		Thread t1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process1Content, this.getClass().getClassLoader(), true);
				defs.add(def);
			}
		});
		
		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				ProcessDefinition def = bpmn2Service.buildProcessDefinition("test", process2Content, this.getClass().getClassLoader(), true);
				defs.add(def);
			}
		});
		
		t1.start();
		t2.start();
		
		Thread.sleep(3000);
		
		assertEquals(2, defs.size());
	}
}
