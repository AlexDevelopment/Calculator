package com.testtask.calculator;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements ShowErrorListener, CleanExpressionListener {

    private ReversePolishNotation mReversePolishNotation;

    private Button[] mButtonsNumber = new Button[20];

    private String mStrExpression = "";
    private String mStrResult = "";

    private TextView mTxtDecision;
    private TextView mTxtExpression;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        mReversePolishNotation = new ReversePolishNotation(this, this);

        Typeface mFont = Typeface.createFromAsset(getAssets(), getString(R.string.path_font_robo));

        ViewGroup rootText = (ViewGroup) findViewById(R.id.text_linear);
        setFont(rootText, mFont);
        ViewGroup rootButton = (ViewGroup) findViewById(R.id.button_linear);
        setFont(rootButton, mFont);

        initView();
    }

    private void initView() {
        mTxtDecision = (TextView) findViewById(R.id.decision);
        mTxtExpression = (TextView) findViewById(R.id.expression);

        mButtonsNumber[0] = (Button) findViewById(R.id.zero);
        mButtonsNumber[1] = (Button) findViewById(R.id.one);
        mButtonsNumber[2] = (Button) findViewById(R.id.two);
        mButtonsNumber[3] = (Button) findViewById(R.id.tree);
        mButtonsNumber[4] = (Button) findViewById(R.id.four);
        mButtonsNumber[5] = (Button) findViewById(R.id.five);
        mButtonsNumber[6] = (Button) findViewById(R.id.six);
        mButtonsNumber[7] = (Button) findViewById(R.id.seven);
        mButtonsNumber[8] = (Button) findViewById(R.id.eight);
        mButtonsNumber[9] = (Button) findViewById(R.id.nine);
        mButtonsNumber[10] = (Button) findViewById(R.id.opening_parenthesis);
        mButtonsNumber[11] = (Button) findViewById(R.id.closing_parenthesis);
        mButtonsNumber[12] = (Button) findViewById(R.id.division);
        mButtonsNumber[13] = (Button) findViewById(R.id.increase);
        mButtonsNumber[14] = (Button) findViewById(R.id.minus);
        mButtonsNumber[15] = (Button) findViewById(R.id.plus);
        mButtonsNumber[16] = (Button) findViewById(R.id.decimal);
        mButtonsNumber[17] = (Button) findViewById(R.id.clean);
        mButtonsNumber[18] = (Button) findViewById(R.id.back);
        mButtonsNumber[19] = (Button) findViewById(R.id.equal);

        initListeners();
    }

    private void initListeners() {
        for (int i = 0; i < mButtonsNumber.length; i++) {
            final String tag = (String) mButtonsNumber[i].getTag();
            mButtonsNumber[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCharInExpression(tag.charAt(0));
                }
            });
        }
    }

    private void setCharInExpression(char input) {
        if (Character.isDigit(input)) {
            setDigitInExpression(input);
        } else if (isOperator(input)) {
            setOperatorInExpression(input);
        } else if (isParenthesis(input)) {
            setParenthesisInExpression(input);
        } else if (isPoint(input)) {
            setPointInExpression();
        } else if (isBack(input)) {
            back();
        } else if (isClean(input)) {
            cleanExpression();
        } else if (isEqual(input)) {
            equal();
        }
    }

    private boolean isOperator(char с) {
        if (("+-/*".indexOf(с) != -1))
            return true;
        return false;
    }

    private boolean isParenthesis(char с) {
        if (("()".indexOf(с) != -1))
            return true;
        return false;
    }

    private boolean isPoint(char с) {
        if ((".".indexOf(с) != -1))
            return true;
        return false;
    }

    private boolean isBack(char с) {
        if (("b".indexOf(с) != -1))
            return true;
        return false;
    }

    private boolean isClean(char с) {
        if (("C".indexOf(с) != -1))
            return true;
        return false;
    }

    private boolean isEqual(char с) {
        if (("=".indexOf(с) != -1))
            return true;
        return false;
    }

    private void setDigitInExpression(char digit) {
        if (mTxtDecision.getText().length() > 0) {
            mTxtDecision.setText("");
        }
        if (mStrExpression.length() > 0 && isCloseParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
            mStrExpression = mStrExpression.concat("*" + digit);
        } else {
            mStrExpression += digit;
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void setOperatorInExpression(char operator) {
        switch (operator) {
            case '-':
                setMinusInExpression();
                break;
            case '+':
                setPlusInExpression();
                break;
            case '*':
                setIncreaseInExpression();
                break;
            case '/':
                setDivisionInExpression();
                break;
        }
    }

    private void setMinusInExpression() {
        if (mStrExpression.length() > 0) {
            if (isNotOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("-");
            } else if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
                mStrExpression = mStrExpression.concat("-");
            }
        } else {
            if (isResult()) {
                mStrExpression = mStrResult;
                mStrExpression = mStrExpression.concat("-");
                mTxtDecision.setText("");
            } else {
                mStrExpression = mStrExpression.concat("-");
            }
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void setPlusInExpression() {
        if (mStrExpression.length() > 0) {
            if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
                showError(getString(R.string.enter_the_number));
            } else if (isNotOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("+");
            } else if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 2))) {
                    showError(getString(R.string.you_can_not_put_plus_after_the_opening_parenthesis));
                } else {
                    mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
                    mStrExpression = mStrExpression.concat("+");
                }
            }
        } else {
            if (isResult()) {
                mStrExpression = mStrResult;
                mStrExpression = mStrExpression.concat("+");
                mTxtDecision.setText("");
            }
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void setIncreaseInExpression() {
        if (mStrExpression.length() > 0) {
            if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
                showError(getString(R.string.enter_the_number));
            } else if (isNotOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("*");
            } else if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 2))) {
                    showError(getString(R.string.you_can_not_put_operator_multiply_after_the_opening_parenthesis));
                } else {
                    mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
                    mStrExpression = mStrExpression.concat("*");
                }
            }
        } else {
            if (isResult()) {
                mStrExpression = mStrResult;
                mStrExpression = mStrExpression.concat("*");
                mTxtDecision.setText("");
            }
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void setDivisionInExpression() {
        if (mStrExpression.length() > 0) {
            if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
                showError(getString(R.string.enter_the_number));
            } else if (isNotOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("/");
            } else if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 2))) {
                    showError(getString(R.string.you_can_not_put_operator_divide_after_the_opening_parenthesis));
                } else {
                    mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
                    mStrExpression = mStrExpression.concat("/");
                }
            }
        } else {
            if (isResult()) {
                mStrExpression = mStrResult;
                mStrExpression = mStrExpression.concat("/");
                mTxtDecision.setText("");
            }
        }
        mTxtExpression.setText(mStrExpression);
    }


    private void setParenthesisInExpression(char parenthesis) {
        switch (parenthesis) {
            case '(':
                setOpeningParenthesisInExpression();
                break;
            case ')':
                setClosingParenthesisInExpression();
                break;
        }
    }

    private void setOpeningParenthesisInExpression() {
        if (mStrExpression.length() > 0) {
            if (Character.isDigit(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("*(");
            } else if (isCloseParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.concat("*(");
            } else {
                mStrExpression = mStrExpression.concat("(");
            }
        } else {
            if (isResult()) {
                mStrExpression = mStrResult;
                mStrExpression = mStrExpression.concat("*(");
                mTxtDecision.setText("");
            } else {
                mStrExpression = mStrExpression.concat("(");
            }
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void setClosingParenthesisInExpression() {
        if (mStrExpression.length() > 0) {
            mTxtDecision.setText("");
            if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
                mStrExpression = mStrExpression.concat(")");
            } else if (isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
                showError(getString(R.string.enter_the_expression_after_the_parentheses));
            } else {
                mStrExpression = mStrExpression.concat(")");
            }
            mTxtExpression.setText(mStrExpression);
        }
    }

    private void setPointInExpression() {
        if (mStrExpression.length() == 0
                || isOperator(mStrExpression.charAt(mStrExpression.length() - 1))
                || isOpenParenthesis(mStrExpression.charAt(mStrExpression.length() - 1))) {
            mTxtDecision.setText("");
            mStrExpression = mStrExpression.concat("0.");
        }
        if (mStrExpression.charAt(mStrExpression.length() - 1) != ')') {
            mStrExpression = mStrExpression.concat(".");
        } else {
            mStrExpression = mStrExpression.concat("*0.");
        }
        if (isNotTwoPoint(mStrExpression)) {
            mTxtExpression.setText(mStrExpression);
        } else {
            mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
        }
        mTxtExpression.setText(mStrExpression);
    }

    private void back() {
        if (mTxtDecision.getText().length() > 0) {
            mStrExpression = "";
        }
        if (mStrExpression.length() > 0) {
            mStrExpression = mStrExpression.substring(0, mStrExpression.length() - 1);
            mTxtExpression.setText(mStrExpression);
        }
    }

    private void equal() {
        String temporaryExpression = mStrExpression;
        if (mTxtExpression.getText().toString().length() > 0) {
            if (isOperator(mStrExpression.charAt(mStrExpression.length() - 1))) {
                showError(getString(R.string.unfinished_expression));
            } else {
                mStrResult = mReversePolishNotation.calculate(mStrExpression);
                if (mStrResult.equals("Infinity") || mStrResult.equals("NaN")) {
                    mTxtDecision.setText(R.string.you_can_not_divide_by_zero);
                    mStrResult = "0";
                } else if (mStrResult.equals("error")) {
                    mStrResult = "";
                } else {
                    mTxtDecision.setText(temporaryExpression + "=" + mStrResult);
                }
            }
        }
    }

    private boolean isNotOperator(char с) {
        if (("+-/*".indexOf(с) != -1))
            return false;
        return true;
    }


    private boolean isOpenParenthesis(char c) {
        if ("(".indexOf(c) != -1)
            return true;
        return false;
    }

    private boolean isCloseParenthesis(char c) {
        if ((")".indexOf(c) != -1))
            return true;
        return false;
    }

    private boolean isResult() {
        if (mTxtDecision.getText().length() > 0) {
            return true;
        }
        return false;
    }

    private boolean isNotTwoPoint(String expression) {
        Pattern pattern = Pattern.compile("\\d*\\.\\d*\\.");
        Matcher matcher = pattern.matcher(expression);
        if (matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    public void setFont(ViewGroup group, Typeface font) {
        int count = group.getChildCount();
        View v;
        for (int i = 0; i < count; i++) {
            v = group.getChildAt(i);
            if (v instanceof TextView || v instanceof EditText || v instanceof Button) {
                ((TextView) v).setTypeface(font);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, font);
        }
    }

    @Override
    public void showError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void cleanExpression() {
        mStrExpression = "";
        mTxtExpression.setText("");
        mTxtDecision.setText("");
    }
}
