package com.example.truyenchuvietsub.dto.series;

import com.example.truyenchuvietsub.dto.UserDTO;

import java.util.Date;

public class RecentCreatedSeries {
    public String id;
    public String title;
    public String slug;
    public UserDTO author;
    public String cover;
    public Date createdAt;
    public Integer totalChapters;
}
