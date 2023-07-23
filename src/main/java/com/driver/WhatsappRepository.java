package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class WhatsappRepository {
    HashMap<String,User> userDb = new HashMap<>();
    HashMap<Group,List<User>> groupUserMap = new HashMap<>();

    HashMap<Group, List<Message>> groupMessageMap = new HashMap<>();
    HashMap<Message, User> senderMap = new HashMap<>();
     HashMap<Group, User> adminMap = new HashMap<>();
//     HashMap<String,User> userMobile = new HashMap<>();
     int customGroupCount;
     int messageId = 0;

    public String createUser(String name, String mobile) {
        User ur = userDb.get(name);
        if(ur != null ) {
            throw new RuntimeException("User already exists");
        }
        User user = new User();
        user.setName(name);
        user.setMobile(mobile);
        userDb.put(mobile,user);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {

        List<User> al = new ArrayList<>(users);
        if(users.size() < 2){
            throw new RuntimeException("Minimum User should be two");
        }else if(users.size() == 2) {
            User ur = al.get(1);

            Group gr = new Group(ur.getName(),2);
            adminMap.put(gr,users.get(0));
            groupUserMap.put(gr,al);
            return gr;
        }else {
            customGroupCount++;
            Group gr = new Group("Group "+customGroupCount,users.size());
            groupUserMap.put(gr,al);
            adminMap.put(gr,users.get(0));
            return gr;
        }
    }

    public int createMessage(String content) {
        Message ms = new Message(messageId,content);
        messageId++;
        return messageId;
    }

    public String changeAdmin(User approver, User user, Group group) {
        if(!groupUserMap.containsKey(group)) {
            throw new RuntimeException("Group does not exist");
        }else if(!adminMap.get(group).equals(approver)) {
            throw new RuntimeException("Approver does not have rights");
        }else if(!groupUserMap.get(group).contains(user)){
            throw new RuntimeException("User is not a participant");
        }else {
            adminMap.put(group,user);
            return "SUCCESS";
        }
    }

    public int removeUser(User user) {
        return 0;
    }

    public int sendMessage(Message message, User sender, Group group) {
        int numberOfMessage = 0;
        if((groupUserMap.containsKey(group)) == false){
            throw new RuntimeException("Group does not exist");
        }
        else if((groupUserMap.get(group).contains(sender)) == false){
            throw new RuntimeException("You are not allowed to send message");
        }
        else {
            List<Message> msg =new ArrayList<>();
            if(groupMessageMap.containsKey(group)) {
                msg = groupMessageMap.get(group);
            }
            msg.add(message);
            groupMessageMap.put(group,msg);
            numberOfMessage = msg.size();
            senderMap.put(message,sender);
        }
        return numberOfMessage;
    }
}
