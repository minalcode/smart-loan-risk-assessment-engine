package com.artificial.smart_loan_risk_assessment_engine.Controller;

import com.artificial.smart_loan_risk_assessment_engine.Service.LoanRiskService;
import com.artificial.smart_loan_risk_assessment_engine.dto.LoanRequestDto;
import com.artificial.smart_loan_risk_assessment_engine.dto.LoanResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
public class LoanRiskController {

    private final LoanRiskService loanRiskService;

    public LoanRiskController(LoanRiskService loanRiskService) {
        this.loanRiskService = loanRiskService;
    }

    @PostMapping("/assess")
    public ResponseEntity<LoanResponseDto> assessLoan(
            @RequestBody LoanRequestDto dto) {

        return ResponseEntity.ok(
                loanRiskService.assessLoan(dto)
        );
    }
}