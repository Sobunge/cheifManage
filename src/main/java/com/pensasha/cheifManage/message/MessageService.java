package com.pensasha.cheifManage.message;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    // Get all messages sent by user
    public List<Message> getMySentMessages(int idNumber) {
        return messageRepository.findAllBySender(idNumber);
    }

    // Get all messages received as a user
    public List<Message> getMyMessages(int idNumber) {
        return messageRepository.findAllByRecieversIdNumber(idNumber);
    }

    //Get all my unread messages
    public List<Message> getMyUnreadMessages(int idNumber, Status status){
        return messageRepository.findAllByRecieversIdNumberAndStatus(idNumber, status);
    }

    // Get a message
    public Message getMyMessage(Long id) {
        return messageRepository.findById(id).get();
    }

    // send a message
    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    // delete a message
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

}
