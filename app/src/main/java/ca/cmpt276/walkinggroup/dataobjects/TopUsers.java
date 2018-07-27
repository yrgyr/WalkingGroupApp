package ca.cmpt276.walkinggroup.dataobjects;

// stores a list of top 100 users ranked by total reward points earned

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TopUsers {
    private List<User> top100 = new ArrayList<>();
    private List<User> allUsersWithPoints = new ArrayList<>();

    Comparator<User> compUsers = (user1, user2) ->
    {
      Integer points1 = user1.getTotalPointsEarned();
      Integer points2 = user2.getTotalPointsEarned();

      if (points1 <= points2){
          return 1;
      }

      return -1;

        //return (points1.compareTo(points2));
    };

    public TopUsers(List<User> allUsers){
        // some users may have reward points of null, need to remove those user first before sorting
        for (int j = 0; j < allUsers.size(); j++){
            User user = allUsers.get(j);
            if (user.getTotalPointsEarned() != null){
                allUsersWithPoints.add(user);
            }
        }

        Collections.sort(allUsersWithPoints, compUsers);

        if (allUsersWithPoints.size() > 100){   // only display the top 100 users if there are more than 100 users
            for (int i = 0; i < 100; i++){
                top100.add(allUsersWithPoints.get(i));
            }
        } else {
            if (allUsersWithPoints.size() > 0) {
                for (int i = 0; i < allUsersWithPoints.size(); i++) {
                    top100.add(allUsersWithPoints.get(i));
                }
            } else {
                top100 = null;
            }
        }
    }

    public List<User> getTop100Users(){
        return top100;
    }
}
