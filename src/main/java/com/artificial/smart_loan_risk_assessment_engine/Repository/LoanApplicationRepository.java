package com.artificial.smart_loan_risk_assessment_engine.Repository;

import com.artificial.smart_loan_risk_assessment_engine.Model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanApplicationRepository
        extends JpaRepository<LoanApplication, Long> {
}