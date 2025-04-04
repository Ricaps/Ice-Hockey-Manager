package cz.fi.muni.pa165.gameservice.business.services;

import cz.fi.muni.pa165.gameservice.api.exception.ValueIsMissingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class TaskSchedulerTest {

	@Mock
	private TaskScheduler taskScheduler;

	@InjectMocks
	private TaskSchedulerService taskSchedulerService;

	@Test
	void scheduleTask_nullRunnable_throwsException() {
		assertThatThrownBy(() -> taskSchedulerService.scheduleTask(null, UUID.randomUUID(), Instant.now()))
			.isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide runnable for the scheduled task");

		Mockito.verify(taskScheduler, Mockito.never()).schedule(Mockito.any(), (Instant) Mockito.any());
	}

	@Test
	void scheduleTask_nullUUID_throwsException() {
		assertThatThrownBy(() -> taskSchedulerService.scheduleTask(() -> {
		}, null, Instant.now())).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide taskID for the scheduled task");

		Mockito.verify(taskScheduler, Mockito.never()).schedule(Mockito.any(), (Instant) Mockito.any());
	}

	@Test
	void scheduleTask_nullStartAt_throwsException() {
		assertThatThrownBy(() -> taskSchedulerService.scheduleTask(() -> {
		}, UUID.randomUUID(), null)).isInstanceOf(ValueIsMissingException.class)
			.hasMessage("Please provide startAt time for the scheduled task");

		Mockito.verify(taskScheduler, Mockito.never()).schedule(Mockito.any(), (Instant) Mockito.any());
	}

	@Test
	@SuppressWarnings("unchecked")
	void scheduleTask_allGood_shouldBeScheduled() {
		var instant = Instant.now();

		Mockito.when(taskScheduler.schedule(Mockito.any(), Mockito.eq(instant)))
			.thenReturn(Mockito.mock(ScheduledFuture.class));

		taskSchedulerService.scheduleTask(() -> {
		}, UUID.randomUUID(), instant);

		Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), Mockito.eq(instant));
	}

	@Test
	@SuppressWarnings("unchecked")
	void scheduleTask_alreadyAssigned_shouldNotBeReScheduled() {
		var instant = Instant.now();

		var mockedFuture = Mockito.mock(ScheduledFuture.class);
		Mockito.when(mockedFuture.state()).thenReturn(Future.State.RUNNING);
		Mockito.when(taskScheduler.schedule(Mockito.any(), Mockito.eq(instant))).thenReturn(mockedFuture);

		var uuid = UUID.randomUUID();
		taskSchedulerService.scheduleTask(() -> {
		}, uuid, instant);
		taskSchedulerService.scheduleTask(() -> {
		}, uuid, instant);

		// taskScheduler.schedule should be called only once when the same task was
		// already scheduled
		Mockito.verify(taskScheduler, Mockito.times(1)).schedule(Mockito.any(), (Instant) Mockito.any());
	}

	@Test
	@SuppressWarnings("unchecked")
	void cancelTask_allGood_shouldBeCanceled() {
		var instant = Instant.now();
		var uuid = UUID.randomUUID();

		var mockedFuture = Mockito.mock(ScheduledFuture.class);
		Mockito.when(taskScheduler.schedule(Mockito.any(), Mockito.eq(instant))).thenReturn(mockedFuture);
		taskSchedulerService.scheduleTask(() -> {
		}, uuid, instant);

		taskSchedulerService.cancelTask(uuid);
		Mockito.verify(mockedFuture, Mockito.times(1)).cancel(Mockito.anyBoolean());
	}

	@Test
	void cancelTask_unscheduledTask_cancelShouldNotBeCalled() {
		var uuid = UUID.randomUUID();
		var mockedFuture = Mockito.mock(ScheduledFuture.class);

		taskSchedulerService.cancelTask(uuid);
		Mockito.verify(mockedFuture, Mockito.never()).cancel(Mockito.anyBoolean());
	}

	@Test
	void cancelTask_noTaskProvided_throwsException() {
		assertThatThrownBy(() -> taskSchedulerService.cancelTask(null)).hasMessage("Please provide taskID to cancel")
			.isInstanceOf(ValueIsMissingException.class);
	}

}