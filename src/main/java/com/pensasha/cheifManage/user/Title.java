package com.pensasha.cheifManage.user;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Title {
    CHIEF("Chief"),
    ASSISTANT_CHIEF("Assistant_chief"),
    MR("Mr"),
    MRS("Mrs"),
    MS("Ms");

    String title;
}
