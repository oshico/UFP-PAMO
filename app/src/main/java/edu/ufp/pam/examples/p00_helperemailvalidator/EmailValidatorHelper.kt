package edu.ufp.pam.examples.p00_helperemailvalidator

import android.text.Editable
import android.text.TextWatcher
import java.util.regex.Pattern


class EmailValidatorHelper : TextWatcher {

    companion object {
        public val EMAIL_PATTERN: Pattern = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
        );

        fun isValidEmail(email: CharSequence): Boolean {
            return EMAIL_PATTERN.matcher(
                email
            ).matches();
        }

        fun isValidPassword(pass: CharSequence): Boolean {
            return isValidPasswordLength(
                pass.toString()
            );
        }

        private fun isValidPasswordLength(pass: String): Boolean {
            if (pass.length > 6) {
                return true;
            }
            return false;
        }
    }

    private var mIsValid: Boolean = false;

    fun isValid(): Boolean {
        return mIsValid;
    }


    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        //Check text before text changes
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        //Check text when text changes
    }

    override fun afterTextChanged(editableText: Editable) {
        mIsValid = isValidEmail(
            editableText
        );
        mIsValid = isValidPassword(
            editableText
        );
    }
}

