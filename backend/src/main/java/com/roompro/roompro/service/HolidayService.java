package com.roompro.roompro.service;


import com.roompro.roompro.dto.request.HolidayDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class HolidayService {

    private final RestTemplate restTemplate;

    @Autowired
    public HolidayService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<HolidayDTO> getHolidays(String year, String country) {
        String url = "https://date.nager.at/api/v3/publicholidays/" + year + "/" + country;
        List<Object> holidaysResponse = restTemplate.getForObject(url, List.class);
        List<HolidayDTO> holidays = new ArrayList<>();

        for (Object holidayObj : holidaysResponse) {

            Map<String, Object> holiday = (Map<String, Object>) holidayObj;

            String date = (String) holiday.get("date");
            String localName = (String) holiday.get("localName");


            HolidayDTO holidayDTO = new HolidayDTO();
            holidayDTO.setDate(date);
            holidayDTO.setName(localName);

            holidays.add(holidayDTO);
        }

        return holidays;
    }
}
