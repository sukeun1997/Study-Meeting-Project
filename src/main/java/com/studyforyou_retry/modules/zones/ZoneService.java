package com.studyforyou_retry.modules.zones;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    private void initZone() {

        if(zoneRepository.count() == 0) {
            ClassPathResource resource = new ClassPathResource("zone_kr.csv");

            try {
                Path path = Path.of(resource.getURI());
                List<Zone> zones = new ArrayList<>();
                Files.readAllLines(path).stream().forEach(list -> {
                    String[] split = list.split(",");
                    Zone zone = Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                    zones.add(zone);
                });
                zoneRepository.saveAll(zones);
            } catch (IOException e) {
                log.error("{}", e.getMessage(), e);
            }
        }
    }
}

