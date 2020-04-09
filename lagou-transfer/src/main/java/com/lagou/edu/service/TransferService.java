package com.lagou.edu.service;

/**
 * @author xsq
 */
public interface TransferService {

    void transfer(String fromCardNo,String toCardNo,int money) throws Exception;
}
