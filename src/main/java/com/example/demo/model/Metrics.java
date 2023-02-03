package com.example.demo.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Metrics {

    private String id;

    private String formula;

    private List<String> params;
}
