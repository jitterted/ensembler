package com.jitterted.mobreg.adapter.out.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jitterted.mobreg.domain.Huddle;
import com.jitterted.mobreg.domain.HuddleId;
import com.jitterted.mobreg.domain.HuddleRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Don't use this, left here for instructional purposes only
 */
@Deprecated
public class FileHuddlePuddle implements HuddleRepository {
    private final ObjectMapper objectMapper;

    public FileHuddlePuddle() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Huddle save(Huddle huddle) {
        List<Huddle> allHuddles = findAll();
        if (huddle.getId() == null) {
            long maxId = allHuddles.stream()
                                   .map(Huddle::getId)
                                   .map(HuddleId::id)
                                   .max(Long::compareTo)
                                   .orElse(0L);
            huddle.setId(HuddleId.of(maxId + 1));
        }
        Map<HuddleId, Huddle> huddleMap = allHuddles.stream()
                                                    .collect(
                                                            Collectors.toMap(
                                                                    Huddle::getId,
                                                                    Function.identity(),
                                                                    (a, b) -> b));
        huddleMap.put(huddle.getId(), huddle);

        List<HuddleDto> huddleDtos = huddleMap.values()
                                              .stream()
                                              .map(HuddleDto::from)
                                              .toList();
        HuddlesDto huddlesDto = new HuddlesDto(huddleDtos);

        try {
            objectMapper.writeValue(new File("huddles.json"), huddlesDto);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return huddle;
    }

    @Override
    public List<Huddle> findAll() {
        HuddlesDto huddlesDto = null;
        try {
            huddlesDto = objectMapper.readValue(new File("huddles.json"), HuddlesDto.class);
        } catch (FileNotFoundException fnfe) {
            return new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return huddlesDto.getHuddleDtos()
                         .stream()
                         .map(HuddleDto::asHuddle)
                         .collect(Collectors.toList());
    }

    @Override
    public Optional<Huddle> findById(HuddleId huddleId) {
        return findAll().stream()
                        .filter(huddle -> huddle.getId().id() == huddleId.id())
                        .findAny();
    }
}
