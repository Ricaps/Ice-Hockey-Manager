package cz.fi.muni.pa165.gameservice.business.services;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.is;

class TaskSchedulerServiceTest {

	private TaskSchedulerService taskSchedulerService;

	@BeforeEach
	void setup() {
		TaskScheduler taskScheduler = new SimpleAsyncTaskScheduler();
		taskSchedulerService = new TaskSchedulerService(taskScheduler);
	}

	@Test
	void scheduleTask_allGood_taskShouldBeScheduled() {
		var called = new AtomicInteger();
		Runnable runnable = called::incrementAndGet;
		var scheduleTime = Instant.now().plusMillis(300);
		var uuid = UUID.randomUUID();

		taskSchedulerService.scheduleTask(runnable, uuid, scheduleTime);
		Awaitility.await().atMost(500, TimeUnit.MILLISECONDS).untilAtomic(called, is(1));
	}

	@Test
	void scheduleTask_scheduledTwice_shouldRunOnce() {
		var called = new AtomicInteger();
		Runnable runnable = called::incrementAndGet;

		var scheduleTime = Instant.now().plusMillis(300);
		var scheduleTimeShorter = Instant.now().plusMillis(100);
		var uuid = UUID.randomUUID();

		taskSchedulerService.scheduleTask(runnable, uuid, scheduleTime);
		taskSchedulerService.scheduleTask(runnable, uuid, scheduleTimeShorter);
		Awaitility.await().atLeast(200, TimeUnit.MILLISECONDS).untilAtomic(called, is(1));
	}

	@Test
	void scheduleTask_scheduledTwiceOneAfterAnother_shouldRunTwoTimes() {
		var called = new AtomicInteger();
		Runnable runnable = called::incrementAndGet;

		var scheduleTime = Instant.now().plusMillis(300);
		var uuid = UUID.randomUUID();

		taskSchedulerService.scheduleTask(runnable, uuid, scheduleTime);
		Awaitility.await().atLeast(250, TimeUnit.MILLISECONDS).untilAtomic(called, is(1));

		var scheduleTimeShorter = Instant.now().plusMillis(500);
		taskSchedulerService.scheduleTask(runnable, uuid, scheduleTimeShorter);
		Awaitility.await().atMost(600, TimeUnit.MILLISECONDS).untilAtomic(called, is(2));
	}

}