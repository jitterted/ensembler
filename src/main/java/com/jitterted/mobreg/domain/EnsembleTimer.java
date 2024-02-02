package com.jitterted.mobreg.domain;

import java.text.MessageFormat;
import java.util.stream.Stream;

public class EnsembleTimer {
    private final EnsembleId ensembleId;
    private final Stream<MemberId> participants;

    public EnsembleTimer(EnsembleId ensembleId, Stream<MemberId> participants) {
        this.ensembleId = ensembleId;
        this.participants = participants;
    }

    public EnsembleId ensembleId() {
        return ensembleId;
    }

    public Stream<MemberId> participants() {
        return participants;
    }

    @Override
    public String toString() {
        return MessageFormat.format(
                "EnsembleTimer [ensembleId={0}, participants={1}]",
                ensembleId, participants);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EnsembleTimer that = (EnsembleTimer) o;

        return ensembleId.equals(that.ensembleId);
    }

    @Override
    public int hashCode() {
        return ensembleId.hashCode();
    }
}
