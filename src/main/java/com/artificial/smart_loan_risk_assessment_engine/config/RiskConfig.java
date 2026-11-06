package com.artificial.smart_loan_risk_assessment_engine.config;

import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class RiskConfig {

    public static final BigDecimal ANNUAL_INTEREST_RATE =
            new BigDecimal("12");

    public static final BigDecimal LOW_DTI =
            new BigDecimal("30");

    public static final BigDecimal MEDIUM_DTI =
            new BigDecimal("40");

    public static final BigDecimal HIGH_DTI =
            new BigDecimal("50");

    public static final BigDecimal MAX_DTI =
            new BigDecimal("60");

    public static final int MIN_CREDIT_SCORE = 550;

    public static final int APPROVAL_SCORE = 70;

    public static final int REVIEW_SCORE = 40;
}