package ca.cmpt276.walkinggroup.dataobjects;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

// contains methods that makes server calls to send/receive data
public class ServerCalls {
    public static List<Group> getGroups(WGServerProxy proxy, Context context){
        List<Group> groups = new ArrayList<>();
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(context, caller, returnedGroups -> response(returnedGroups, groups));

        return groups;
    }

    private static void response(List<Group> returnedGroups, List<Group> groups) {
        groups = returnedGroups;
    }
}
