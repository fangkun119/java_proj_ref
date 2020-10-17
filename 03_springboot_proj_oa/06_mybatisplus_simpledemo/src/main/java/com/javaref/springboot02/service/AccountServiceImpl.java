package com.javaref.springboot02.service;

import com.javaref.springboot02.entity.Account;
import com.javaref.springboot02.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account>  implements IAccountService {
}
