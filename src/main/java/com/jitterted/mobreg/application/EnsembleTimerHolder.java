package com.jitterted.mobreg.application;

import com.jitterted.mobreg.application.port.Broadcaster;
import com.jitterted.mobreg.application.port.EnsembleRepository;
import com.jitterted.mobreg.application.port.MemberRepository;
import com.jitterted.mobreg.application.port.SecondsTicker;
import com.jitterted.mobreg.domain.Ensemble;
import com.jitterted.mobreg.domain.EnsembleId;
import com.jitterted.mobreg.domain.EnsembleTimer;
import com.jitterted.mobreg.domain.Member;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

public class EnsembleTimerHolder implements EnsembleTimerTickHandler {
    private final EnsembleRepository ensembleRepository;
    private final MemberRepository memberRepository;
    private final Broadcaster broadcaster;
    private final SingleEntryHashMap<EnsembleId, EnsembleTimer> ensembleTimers = new SingleEntryHashMap<>();
    private final SecondsTicker secondsTicker;

    public EnsembleTimerHolder(EnsembleRepository ensembleRepository,
                               MemberRepository memberRepository,
                               Broadcaster broadcaster,
                               SecondsTicker secondsTicker) {
        this.ensembleRepository = ensembleRepository;
        this.memberRepository = memberRepository;
        this.broadcaster = broadcaster;
        this.secondsTicker = secondsTicker;
    }

    public static EnsembleTimerHolder createNull(EnsembleRepository ensembleRepository, MemberRepository memberRepository) {
        return new EnsembleTimerHolder(ensembleRepository,
                                       memberRepository,
                                       ensembleTimer -> {},
                                       new DoNothingSecondsTicker());
    }

    @NotNull
    public EnsembleTimer timerFor(EnsembleId ensembleId) {
        if (!ensembleTimers.containsKey(ensembleId)) {
            throw new IllegalStateException("No Ensemble Timer exists for Ensemble %d.".formatted(ensembleId.id()));
        }
        return ensembleTimers.get(ensembleId);
    }

    @NotNull
    public EnsembleTimer createTimerFor(EnsembleId ensembleId) {
        Ensemble ensemble = ensembleRepository.findById(ensembleId)
                                              .orElseThrow();
        EnsembleTimer ensembleTimer = new EnsembleTimer(ensembleId,
                                                        ensemble.name(),
                                                        membersFrom(ensemble));
        ensembleTimers.put(ensembleId, ensembleTimer);
        broadcaster.sendCurrentTimer(ensembleTimer);
        return ensembleTimer;
    }

    private List<Member> membersFrom(Ensemble ensemble) {
        return ensemble.participants()
                       .map(memberId -> memberRepository.findById(memberId).orElseThrow())
                       .toList();
    }

    public boolean hasTimerFor(EnsembleId ensembleId) {
        return ensembleTimers.containsKey(ensembleId);
    }

    public boolean isTimerRunningFor(EnsembleId ensembleId) {
        requireTimerToExistFor(ensembleId);
        return ensembleTimers
                .get(ensembleId)
                .state() == EnsembleTimer.TimerState.RUNNING;
    }

    public void startTimerFor(EnsembleId ensembleId, Instant timeStarted) {
        requireTimerToExistFor(ensembleId);

        ensembleTimers.get(ensembleId)
                      .startTimerAt(timeStarted);

        secondsTicker.start(ensembleId, this);
    }

    @Override
    public void handleTickFor(EnsembleId ensembleId, Instant now) {
        EnsembleTimer ensembleTimer = timerFor(ensembleId);
        ensembleTimer.tick(now);

        if (ensembleTimer.state() == EnsembleTimer.TimerState.FINISHED) {
            secondsTicker.stop();
        }

        broadcaster.sendCurrentTimer(ensembleTimer);
    }


    private void requireTimerToExistFor(EnsembleId ensembleId) {
        if (!hasTimerFor(ensembleId)) {
            throw new IllegalArgumentException(
                    "No timer for Ensemble ID %d exists.".formatted(ensembleId.id()));
        }
    }

    public void rotateTimerFor(EnsembleId ensembleId) {
        EnsembleTimer ensembleTimer = timerFor(ensembleId);
        ensembleTimer.rotateRoles();

        broadcaster.sendCurrentTimer(ensembleTimer);
    }

    static class SingleEntryHashMap<K, V> extends HashMap<K, V> {
        @Override
        public V put(K key, V value) {
            if (this.size() == 1 && !this.containsKey(key)) {
                throw new IllegalStateException("A SingleEntryHashMap cannot have more than one entry, has entry for %s, attempting to add entry for %s"
                                                        .formatted(keySet().iterator().next(), key));
            }
            return super.put(key, value);
        }
    }

}
