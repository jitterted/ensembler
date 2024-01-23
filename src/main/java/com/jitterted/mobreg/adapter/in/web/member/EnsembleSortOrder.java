package com.jitterted.mobreg.adapter.in.web.member;

import com.jitterted.mobreg.domain.Ensemble;

import java.util.Comparator;

import static java.util.Comparator.comparing;

public enum EnsembleSortOrder {
    ASCENDING_ORDER(comparing(Ensemble::startDateTime)),
    DESCENDING_ORDER(comparing(Ensemble::startDateTime).reversed());

    private final Comparator<Ensemble> comparator;

    EnsembleSortOrder(Comparator<Ensemble> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Ensemble> comparator() {
        return comparator;
    }
}