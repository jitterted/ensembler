package com.jitterted.mobreg.adapter.in.web.admin;

import com.jitterted.mobreg.application.EnsembleTimerHolder;
import com.jitterted.mobreg.application.NoOpShuffler;
import com.jitterted.mobreg.application.TestEnsembleServiceBuilder;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleBuilder;
import com.jitterted.mobreg.domain.EnsembleId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("unchecked")
class EnsembleTimerLifecycleControllerTest {

    @Nested
    class Lifecycle {
        @Test
        void createAndRedirectToTimerSessionForSpecificEnsemble() {
            Fixture fixture = createEnsembleAndTimerHolder(87);

            String redirectPage = fixture.ensembleTimerController()
                                         .createTimer(87L, null);

            assertThat(redirectPage)
                    .isEqualTo("redirect:/member/timer-view/87");
            assertThat(fixture.ensembleTimerHolder()
                              .hasTimerFor(EnsembleId.of(87)))
                    .isTrue();
        }

        @Test
        void updatedTimerStateHtmlReturnedWhenExistingTimerDeleted() {
            Fixture fixture = createEnsembleAndTimerHolder(135);
            fixture.ensembleTimerHolder()
                   .createTimerFor(EnsembleId.of(135), new NoOpShuffler());

            String actualHtml = fixture.ensembleTimerController()
                                       .deleteTimer(135L);

            assertThat(fixture.ensembleTimerHolder()
                              .hasTimerFor(EnsembleId.of(135)))
                    .isFalse();
            assertThat(actualHtml)
                    .contains("Create 4-Minute Timer")
                    .contains("Create 5-Minute Timer");
        }

        private Fixture createEnsembleAndTimerHolder(int id) {
            Ensemble ensemble = new EnsembleBuilder().id(id)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble)
                    .withThreeParticipants();
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
            EnsembleTimerLifecycleController ensembleTimerController = new EnsembleTimerLifecycleController(ensembleTimerHolder);
            return new Fixture(ensembleTimerHolder, ensembleTimerController);
        }

        private record Fixture(EnsembleTimerHolder ensembleTimerHolder,
                               EnsembleTimerLifecycleController ensembleTimerController) {
        }

    }

    @Nested
    class Htmx {
        @Test
        void returnsOnlyCreateButtonWhenNoTimerExists() {
            EnsembleTimerLifecycleController ensembleTimerController = createEnsembleAndTimerHolder(109);

            String actualHtml = ensembleTimerController.timerState(109L);

            String expectedHtml = """
                                  <swap id="timer-status-container" hx-swap-oob="innerHTML">
                                      <p>No timer currently exists for this Ensemble.</p>
                                  </swap>
                                  <swap id="timer-button-container" hx-swap-oob="innerHTML">
                                      <div class="flex flex-col space-y-4">
                                          <form action="/admin/create-timer/109" method="post">
                                              <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                                              >
                                                  Create 4-Minute Timer
                                              </button>
                                          </form>
                                          <form action="/admin/create-timer/109" method="post">
                                              <input type="hidden" name="duration" value="5">
                                              <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                                              >
                                                  Create 5-Minute Timer
                                              </button>
                                          </form>
                                      </div>
                                  </swap>
                                  """;
            assertThat(actualHtml)
                    .isEqualTo(expectedHtml);
        }

        private static EnsembleTimerLifecycleController createEnsembleAndTimerHolder(int ensembleId) {
            Ensemble ensemble = new EnsembleBuilder().id(ensembleId)
                                                     .startsNow()
                                                     .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensemble)
                    .withThreeParticipants();
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
            return new EnsembleTimerLifecycleController(ensembleTimerHolder);
        }

        @Test
        void returnsOnlyDeleteButtonAndLinkToTimerWhenTimerExistsForThisEnsemble() {
            EnsembleTimerLifecycleController ensembleTimerController = createEnsembleAndTimerHolder(362);
            ensembleTimerController.createTimer(362L, null);

            String actualHtml = ensembleTimerController.timerState(362L);

            String expectedHtml = """
                                  <swap id="timer-status-container" hx-swap-oob="innerHTML">
                                      <p>A timer is currently running for this Ensemble
                                          <a class="underline font-semibold text-blue-600"
                                             href="/member/timer-view/362">here</a>.
                                      </p>
                                  </swap>
                                  <swap id="timer-button-container" hx-swap-oob="innerHTML">
                                      <button class="inline-flex justify-center rounded-md border border-transparent bg-red-500 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-300 focus:ring-offset-2"
                                              hx-post="/admin/delete-timer/362"
                                      >
                                          Delete Timer
                                      </button>
                                  </swap>
                                  """;

            assertThat(actualHtml)
                    .isEqualTo(expectedHtml);
        }

        @Test
        void messageWithLinkToOtherEnsembleWhenAnotherEnsembleTimerAlreadyExists() {
            long ensembleWithoutTimerId = 581L;
            Ensemble ensembleWithoutTimer = new EnsembleBuilder().id(ensembleWithoutTimerId)
                                                                 .startsNow()
                                                                 .build();
            int ensembleWithTimerId = 97;
            Ensemble ensembleWithTimer = new EnsembleBuilder().id(ensembleWithTimerId)
                                                              .startsNow()
                                                              .build();
            TestEnsembleServiceBuilder builder = new TestEnsembleServiceBuilder()
                    .saveEnsemble(ensembleWithoutTimer)
                    .saveEnsemble(ensembleWithTimer)
                    .withThreeParticipants();
            EnsembleTimerHolder ensembleTimerHolder = EnsembleTimerHolder.createNull(builder.ensembleRepository(), builder.memberRepository());
            ensembleTimerHolder.createTimerFor(ensembleWithTimer.getId(), new NoOpShuffler());
            EnsembleTimerLifecycleController ensembleTimerController = new EnsembleTimerLifecycleController(ensembleTimerHolder);

            String actualHtml = ensembleTimerController
                    .timerState(ensembleWithoutTimerId);

            String expectedHtml = """
                                  <swap id="timer-status-container" hx-swap-oob="innerHTML">
                                      <p>Timer exists for
                                          <a class="underline font-semibold text-blue-600"
                                          href="/admin/ensemble/97">another ensemble</a>.
                                          Create will replace that timer with a new one for this Ensemble.
                                      </p>
                                  </swap>
                                  <swap id="timer-button-container" hx-swap-oob="innerHTML">
                                      <div class="flex flex-col space-y-4">
                                          <form action="/admin/create-timer/581" method="post">
                                              <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                                              >
                                                  Create 4-Minute Timer
                                              </button>
                                          </form>
                                          <form action="/admin/create-timer/581" method="post">
                                              <input type="hidden" name="duration" value="5">
                                              <button class="inline-flex items-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-semibold text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-500"
                                              >
                                                  Create 5-Minute Timer
                                              </button>
                                          </form>
                                      </div>
                                  </swap>
                                  """;

            assertThat(actualHtml)
                    .isEqualTo(expectedHtml);
        }
    }

}
