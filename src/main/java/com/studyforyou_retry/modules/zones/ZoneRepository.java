package com.studyforyou_retry.modules.zones;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ZoneRepository extends JpaRepository<Zone,Long> {

    Zone findByCityAndLocalNameOfCity(String city, String localNameOfCity);

}
