package com.example.music.service.impl;

import com.example.music.controller.cmd.RegisterCmd;
import com.example.music.controller.cmd.UserCmd;
import com.example.music.entity.User;
import com.example.music.entity.UserToken;
import com.example.music.enums.UserStatusEnum;
import com.example.music.exception.*;
import com.example.music.intergration.EmailUtil;
import com.example.music.mapper.UserMapper;
import com.example.music.repository.MusicInterestsRepository;
import com.example.music.repository.UserActivationRepository;
import com.example.music.repository.UserRepository;
import com.example.music.repository.UserTokenRepository;
import com.example.music.service.UserService;
import com.example.music.util.ActivateUtil;
import com.example.music.util.MD5Util;
import constant.MusicConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.music.enums.UserStatusEnum.INIT;
import static com.example.music.enums.UserStatusEnum.NORMAL;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EmailUtil emailUtil;
    @Autowired
    private UserActivationRepository userActivationRepository;
    @Autowired
    private UserTokenRepository userTokenRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MusicInterestsRepository musicInterestsRepository;
    @Override
    @Transactional
    public void register(RegisterCmd cmd) {
        User user = userMapper.queryByName(cmd.getName());
        if(user!=null){
            log.warn("{} is registered",cmd.getName());
            throw new UserDuplicatedRegisterException("username is registered");
        }
        userMapper.addUser(buildUser(cmd));
        User userInDB = userMapper.queryByName(cmd.getName());
        String content = MusicConstant.EMAIL_HTML;
        String code = ActivateUtil.generate();
        String finalContent = content.replace("{{code}}", code);
        try {
            emailUtil.sendHtmlMail(cmd.getEmail(), "activate", finalContent);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new EmailFailActivatedException("mail is fail to send");
        }
        userActivationRepository.setActivationCode(cmd.getName(),code);

        Set<String> oldSet = new HashSet<>();
        Set<String> newSet = convertToInterestToSet(cmd.getInterests());
        Set<String> tempOldSet = new HashSet<>(oldSet);
        Set<String> tempNewSet = new HashSet<>(newSet);
        //现在的tempOldSet就是我们要删的
        tempOldSet.removeAll(tempNewSet);
        //这是要更新的
        tempNewSet.removeAll(oldSet);
        for (String delete : tempOldSet){
            musicInterestsRepository.deleteItem(delete, userInDB.getId());
        }
        for(String insert : tempNewSet){
            musicInterestsRepository.addItem(insert, userInDB.getId());
        }
    }

    @Override
    public void activate(String name, String code) {
        User user = userMapper.queryByName(name);
        if(user==null){
            throw new UserNotExistException("user is not existed");
        }
        if(UserStatusEnum.NORMAL.getCode().equals(user.getStatus())){
            return;
        }
        if(UserStatusEnum.ABNORMAL.getCode().equals(user.getStatus())){
            throw new UserNotAllowedException("user is forbidden");
        }

        String activationCode = userActivationRepository.getActivationCode(name);
        if(!code.equals(activationCode)){
            throw new UserFailActivatedException("activation is not verified");
        }
        user.setStatus(UserStatusEnum.NORMAL.getCode());
        userMapper.modifyUser(user);
    }

    @Override
    public void login(String name, String password) {
        User user = userMapper.queryByName(name);
        if(user==null){
            throw new UserNotExistException("user is not existed");
        }
        if(user.getStatus().equals(UserStatusEnum.ABNORMAL.getCode())){
            throw new UserNotAllowedException("user is not normal");
        }
        if(!MD5Util.md5(password).equals(user.getPassword())){
            throw new UserPasswordErrorException("user cannot login because of wrong password");
        }
        userTokenRepository.addToken(user.getId(), buildToken(user));
    }

    @Override
    public User queryByName(String name) {
        User user = userRepository.get(name);
        if(user==null){
            user = userMapper.queryByName(name);
            userRepository.add(name,user);
        }
        return user;
    }

    @Override
    public User queryById(int id) {
        User user = userRepository.get(id);
        if(user==null){
            user = userMapper.queryById(id);
            userRepository.add(id,user);
        }
        return user;
    }

    @Transactional
    @Override
    public void modifyUser(UserCmd cmd) {
        User user = userMapper.queryByName(cmd.getName());
        if(user ==null){
            throw new UserNotExistException("user is not existed");
        }
        if(user.getStatus().equals(UserStatusEnum.ABNORMAL.getCode())){
            throw new UserNotAllowedException("user is not normal");
        }
        user.setName(cmd.getName());
        user.setAge(cmd.getAge());
        user.setId(cmd.getId());
        user.setEmail(cmd.getEmail());
        user.setStatus(cmd.getStatus());
        user.setPassword(cmd.getPassword());
        user.setInterests(cmd.getInterests());
        userMapper.modifyUser(user);
        userRepository.delete(user);

        String oldInterests = user.getInterests();
        Set<String> oldSet = convertToInterestToSet(oldInterests);
        Set<String> newSet = convertToInterestToSet( cmd.getInterests());
        Set<String> tempOldSet = new HashSet<>(oldSet);
        Set<String> tempNewSet = new HashSet<>(newSet);
        //现在的tempOldSet就是我们要删的
        tempOldSet.removeAll(tempNewSet);
        //这是要更新的
        tempNewSet.removeAll(oldSet);
        for (String delete : tempOldSet){
            musicInterestsRepository.deleteItem(delete, user.getId());
        }
        for(String insert : tempNewSet){
            musicInterestsRepository.addItem(insert, user.getId());
        }
    }

    @Transactional
    @Override
    public void modifyUserByAdmin(UserCmd cmd) {
        User user = userMapper.queryByName(cmd.getName());
        if (user == null) {
            throw new UserNotExistException("user is not existed");
        }
        String oldInterests = user.getInterests();
        user.setName(cmd.getName());
        user.setAge(cmd.getAge());
        user.setId(cmd.getId());
        user.setEmail(cmd.getEmail());
        user.setStatus(cmd.getStatus());
        user.setPassword(cmd.getPassword());
        user.setInterests(cmd.getInterests());
        userMapper.modifyUser(user);
        userRepository.delete(user);

        Set<String> oldSet = convertToInterestToSet(oldInterests);
        Set<String> newSet = convertToInterestToSet( cmd.getInterests());
        Set<String> tempOldSet = new HashSet<>(oldSet);
        Set<String> tempNewSet = new HashSet<>(newSet);
        //现在的tempOldSet就是我们要删的
        tempOldSet.removeAll(tempNewSet);
        //这是要更新的
        tempNewSet.removeAll(oldSet);
        for (String delete : tempOldSet){
            musicInterestsRepository.deleteItem(delete, user.getId());
        }
        for(String insert : tempNewSet){
            musicInterestsRepository.addItem(insert, user.getId());
        }
    }

    @Transactional
    @Override
    public void addUser(UserCmd cmd) {
        User existing = userMapper.queryByName(cmd.getName());
        if (existing != null) {
            throw new UserDuplicatedRegisterException("username is registered");
        }
        User user = new User();
        user.setName(cmd.getName());
        user.setPassword(MD5Util.md5(cmd.getPassword()));
        user.setAge(cmd.getAge());
        user.setInterests(cmd.getInterests());
        user.setEmail(cmd.getEmail());
        user.setStatus(cmd.getStatus() == null || cmd.getStatus().isEmpty()
                ? NORMAL.getCode() : cmd.getStatus());
        userMapper.addUser(user);

        User userInDB = userMapper.queryByName(cmd.getName());

        Set<String> oldSet = new HashSet<>();
        Set<String> newSet = convertToInterestToSet(cmd.getInterests());
        Set<String> tempOldSet = new HashSet<>(oldSet);
        Set<String> tempNewSet = new HashSet<>(newSet);
        //现在的tempOldSet就是我们要删的
        tempOldSet.removeAll(tempNewSet);
        //这是要更新的
        tempNewSet.removeAll(oldSet);
        for (String delete : tempOldSet){
            musicInterestsRepository.deleteItem(delete, userInDB.getId());
        }
        for(String insert : tempNewSet){
            musicInterestsRepository.addItem(insert, userInDB.getId());
        }
    }

    @Override
    public List<User> queryAll(int start, int pageSize) {
        return userMapper.queryAll(start, pageSize);
    }

    @Transactional
    @Override
    public void deleteUser(int id) {
        User user = userMapper.queryById(id);
        if(user==null){
            throw new UserNotExistException("user is not existed");
        }
        userMapper.deleteUser(id);
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public void updateInterests(int id, String interests) {
        User user = userMapper.queryById(id);
        if(user==null) {
            throw new UserNotExistException("user is not existed");
        }
        String oldInterests = user.getInterests();
        Set<String> oldSet = convertToInterestToSet(oldInterests);
        Set<String> newSet = convertToInterestToSet(interests);
        Set<String> tempOldSet = new HashSet<>(oldSet);
        Set<String> tempNewSet = new HashSet<>(newSet);
        //现在的tempOldSet就是我们要删的
        tempOldSet.removeAll(tempNewSet);
        //这是要更新的
        tempNewSet.removeAll(oldSet);
        user.setInterests(interests);
        userMapper.modifyUser(user);
        userRepository.delete(user);
        for (String delete : tempOldSet){
            musicInterestsRepository.deleteItem(delete, user.getId());
        }
        for(String insert : tempNewSet){
            musicInterestsRepository.addItem(insert, user.getId());
        }
    }

    private User buildUser(RegisterCmd cmd){
        User user = new User();
        user.setName(cmd.getName());
        user.setPassword(MD5Util.md5(cmd.getPassword()));
        user.setAge(cmd.getAge());
        user.setInterests(cmd.getInterests());
        user.setEmail(cmd.getEmail());
        user.setStatus(INIT.getCode());

        return user;
    }

    private UserToken buildToken(User user){
        UserToken userToken = new UserToken();
        userToken.setUserId(user.getId());
        userToken.setName(user.getName());
        return userToken;
    }

    private Set<String> convertToInterestToSet(String interest){
        String[] interestsArray = interest.split(",");
        Set<String> interestSet = new HashSet<>();
        for (int i = 0; i < interestsArray.length; i++) {
            interestSet.add(interestsArray[i]);
        }
        return interestSet;
    }
}
