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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestContext;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;


/**
 * TODO
 * 
 * @author Frank Scheffler
 */
@RunWith(MockitoJUnitRunner.class)
public class ApplicationEventTestExecutionListenerTest {

	@Mock(answer=Answers.RETURNS_DEEP_STUBS)
	private TestContext testContext;
	
	@Captor
	private ArgumentCaptor<TestExecutionEvent> testExecutionEvent;
	
	
	private final ApplicationEventTestExecutionListener cut = new ApplicationEventTestExecutionListener();
	
	private <T extends TestExecutionEvent> void assertEvent(Class<T> eventClass) {
		verify(testContext.getApplicationContext(), only()).publishEvent(testExecutionEvent.capture());
		assertThat(testExecutionEvent.getValue(), instanceOf(eventClass));
		assertThat(testExecutionEvent.getValue().getSource(), equalTo(testContext));
	}
	
	@Test
	public void publishBeforeClassTestExecutionEvent() throws Exception {
		cut.beforeTestClass(testContext);
		assertEvent(BeforeClassTestExecutionEvent.class);
	}
	
	@Test
	public void publishPrepareInstanceTestExecutionEvent() throws Exception {
		cut.prepareTestInstance(testContext);
		assertEvent(PrepareInstanceTestExecutionEvent.class);
	}

	@Test
	public void publishBeforeMethodTestExecutionEvent() throws Exception {
		cut.beforeTestMethod(testContext);
		assertEvent(BeforeMethodTestExecutionEvent.class);
	}

	@Test
	public void publishAfterMethodTestExecutionEvent() throws Exception {
		cut.afterTestMethod(testContext);
		assertEvent(AfterMethodTestExecutionEvent.class);
	}

	@Test
	public void publishAfterClassTestExecutionEvent() throws Exception {
		cut.afterTestClass(testContext);
		assertEvent(AfterClassTestExecutionEvent.class);
	}
}
