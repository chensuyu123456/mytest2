package com.shellshellfish.aaas.datacollection.server.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "fundcodes")
public class FundCodes {
  String id;
  String code;
  String name;
  String date;
}
