package com.roompro.roompro.service;


import com.roompro.roompro.dto.response.UsersStatsResponseDTO;
import com.roompro.roompro.model.UserLogins;
import com.roompro.roompro.model.Users;
import com.roompro.roompro.repository.UserLoginsRepository;
import com.roompro.roompro.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserLoginsRepository userLoginsRepository;

    @Autowired
    UserRepository userRepository;


    public UsersStatsResponseDTO getUserStats(int year, int month){
        UsersStatsResponseDTO usersStats = new UsersStatsResponseDTO();

        List<Users> users = userRepository.findAll();
        List<UserLogins> userLogins = userLoginsRepository.findAll();

        List<UserLogins> userLoginsM = userLogins.stream().filter(ul->ul.getLoginTimestamp().getMonthValue()==month &&
                ul.getLoginTimestamp().getYear()==year).collect(Collectors.toList());

        // Total user
        usersStats.setTotalUser(users.size());

        // Total logins in the current month
        usersStats.setTotalLoginTheCurrentMonth(userLoginsM.size());

        // Total logins by username
        Map<Long, List<UserLogins>> loginsByUserId = userLogins.stream().filter(ul->ul.getLoginTimestamp().getMonthValue()==month &&
                ul.getLoginTimestamp().getYear()==year).collect(Collectors.groupingBy(userLogin -> userLogin.getUser().getUserId()));

        Map<String, Integer> loginCountsByName = new HashMap<>();
        String mostLoggedName = "";
        int maxLoginCount = 0;

        String leastLoggedName = "";
        int minLoginCount = Integer.MAX_VALUE;

        for(Users user: users){

            Long userId = user.getUserId();
            List<UserLogins> logins = loginsByUserId.get(userId);

            if(logins==null){
                String name = user.getFirstName() + " " + user.getLastName();
                loginCountsByName.put(name, 0);
                continue;
            }

            String name = logins.get(0).getUser().getFirstName() + " " + logins.get(0).getUser().getLastName();
            loginCountsByName.put(name, logins.size());

            if(logins.size()>maxLoginCount){
                maxLoginCount = logins.size();
                mostLoggedName = name;
            }

            if(logins.size()<minLoginCount){
                minLoginCount = logins.size();
                leastLoggedName = name;
            }
        }

        usersStats.setLoginCountsByName(loginCountsByName);
        usersStats.setUserWithMostLogging(Map.of(mostLoggedName, maxLoginCount));
        usersStats.setUserWithLeastLogging(Map.of(leastLoggedName, minLoginCount));

        double avgLoginsPerUserThisMonth = (double) userLoginsM.size() / users.size();
        usersStats.setAverageLoginsPerUser(avgLoginsPerUserThisMonth);

        Map<Integer, Long> loginsByHour = userLogins.stream()
                .collect(Collectors.groupingBy(
                        ul -> ul.getLoginTimestamp().getHour(), // extract hour
                        Collectors.counting()                   // count occurrences
                ));


        Optional<Map.Entry<Integer, Long>> maxEntry = loginsByHour.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (maxEntry.isPresent()) {
            int mostLoggedHour = maxEntry.get().getKey();
            int loginCount = maxEntry.get().getValue().intValue();
            usersStats.setMostLoggedHour(Map.of(mostLoggedHour, loginCount));
        }

        return usersStats;

    }


}
