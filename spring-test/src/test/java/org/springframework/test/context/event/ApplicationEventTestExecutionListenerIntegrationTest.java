/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.event;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestContextTestUtils;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;
import org.springframework.test.context.cache.DefaultContextCache;
import org.springframework.test.context.event.ApplicationEventTestExecutionListenerIntegrationTest.EventCaptureConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ReflectionUtils;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * 
 * @author maverick
 */
public class ApplicationEventTestExecutionListenerIntegrationTest {

	
	@Configuration
	static class EventCaptureConfiguration {
		@Bean
		public TestExecutionListener trigger() {
			return mock(TestExecutionListener.class);
		}
		
		@BeforeTestClass
		public void beforeTestClass(BeforeClassTestExecutionEvent e) throws Exception {
			trigger().beforeTestClass(e.getSource());
		}
		
		@PrepareTestInstance
		public void prepareTestInstance(PrepareInstanceTestExecutionEvent e) throws Exception {
			trigger().prepareTestInstance(e.getSource());
		}
		
		
		@BeforeTestMethod
		public void beforeTestMethod(BeforeMethodTestExecutionEvent e) throws Exception {
			trigger().beforeTestMethod(e.getSource());
		}
		
		@AfterTestMethod
		public void afterTestMethod(AfterMethodTestExecutionEvent e) throws Exception {
			trigger().afterTestMethod(e.getSource());
		}
		
		@AfterTestClass
		public void afterTestClass(AfterClassTestExecutionEvent e) throws Exception {
			trigger().afterTestClass(e.getSource());
		}
		
	}
	
	@ContextConfiguration(classes=EventCaptureConfiguration.class)
	static class EmptyTestCase {
		
		public void dummyMethod() {
		}
	}
	
	static class TestContextExposingTestContextManager extends TestContextManager {
		public TestContextExposingTestContextManager() {
			super(EmptyTestCase.class);
		}
		
		public TestContext getProtectedTestContext() {
			return getTestContext();
		}
	}

	private TestContextManager testContextManager;
	private TestContext testContext;
	private TestExecutionListener trigger;
	private Object testInstance;
	private Method testMethod;
	
	@Before
	public void initialize() {
		TestContextExposingTestContextManager tcm = new TestContextExposingTestContextManager();
		testContextManager=tcm;
		testContext = tcm.getProtectedTestContext();
		trigger = testContext.getApplicationContext().getBean(EventCaptureConfiguration.class).trigger();
		// reset because mock is a cached context bean
		reset(trigger);
		testInstance = new EmptyTestCase();
		testMethod = ReflectionUtils.findMethod(EmptyTestCase.class, "dummyMethod");
	}
	
	@Test
	public void beforeTestClassAnnotation() throws Exception {
		testContextManager.beforeTestClass();
		verify(trigger, only()).beforeTestClass(testContext);
	}
	
	@Test
	public void prepareTestInstanceAnnotation() throws Exception {
		testContextManager.prepareTestInstance(testInstance);
		verify(trigger, only()).prepareTestInstance(testContext);
	}
	
	@Test
	public void beforeTestMethodAnnotation() throws Exception {
		testContextManager.beforeTestMethod(testInstance, testMethod);
		verify(trigger, only()).beforeTestMethod(testContext);
	}
	
	@Test
	public void afterTestMethodAnnotation() throws Exception {
		testContextManager.afterTestMethod(testInstance, testMethod, null);
		verify(trigger, only()).afterTestMethod(testContext);
	}
	
	@Test
	public void afterTestClassAnnotation() throws Exception {
		testContextManager.afterTestClass();
		verify(trigger, only()).afterTestClass(testContext);
	}
}
