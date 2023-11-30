package com.example.demo.service;

import com.example.demo.entity.Resources;

import java.util.List;
// 5-5
public interface ResourcesService {

    Resources getResources(long id);

    List<Resources> getResources();

    void createResources(Resources Resources);

    void deleteResources(long id);
}
