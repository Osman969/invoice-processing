package it.arrive.invoicesystem.invoice.validator

import it.arrive.invoicesystem.invoice.model.PaymentMethod
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

class InvoiceValidatorTest extends Specification {

    def validator

    def "setup"() {
        validator = new InvoiceValidator()
    }

    @Unroll
    def "validatePageNumber does not throw exception for valid page number '#pageNumber'"() {
        when:
            validator.validatePageNumber( pageNumber )
        then:
            noExceptionThrown()
        where:
            pageNumber << [0, 1]
    }

    def "validatePageNumber throws exception for negative page number"() {
        when:
            validator.validatePageNumber( -1 )
        then:
            def ex = thrown( IllegalArgumentException )
            ex.message == "Page number '-1' is invalid. Page number must be greater than or equal to 0"
    }

    @Unroll
    def "validateItemSkuCode throws exception when SKU code is '#input'"() {
        when:
            validator.validateItemSkuCode( input )
        then:
            def ex = thrown( IllegalArgumentException )
            ex.message.contains( "Item SKU code" )
        where:
            input << [null, StringUtils.EMPTY, "   "]
    }

    def "validateItemSkuCode does nothing when SKU code is valid"() {
        when:
            validator.validateItemSkuCode( "SKU123" )
        then:
            noExceptionThrown()
    }

    def "should pass validation for valid PaymentInfoDto"() {
        when:
            validator.validatePaymentMethod( PaymentMethod.CREDIT_CARD )
        then:
            noExceptionThrown()
    }

    def "should throw IllegalArgumentException when paymentMethod is null"() {
        when:
            validator.validatePaymentMethod( null )
        then:
            def ex = thrown( IllegalArgumentException )
            ex.message == "Payment method cannot be null"
    }

    @Unroll
    def "validateCustomerEmail throws exception for invalid email '#input'"() {
        when:
            validator.validateCustomerEmail( input )
        then:
            def ex = thrown( IllegalArgumentException )
            ex.message == "Customer email '%s' is invalid".formatted( input )
        where:
            input << [
                    null,
                    StringUtils.EMPTY,
                    "   ",
                    "plainaddress",
                    "@no-local-part.com",
                    "no-at-symbol.com",
                    "invalid@domain",
                    "invalid@.com",
                    "a@b.c",
                    "invalid@domain..com"
            ]
    }

    @Unroll
    def "validateCustomerEmail does not throw for valid email '#email'"() {
        when:
            validator.validateCustomerEmail( email )
        then:
            noExceptionThrown()
        where:
            email << [
                    "john.doe@example.com",
                    "user123@domain.co",
                    "user.name+tag@sub.domain.org",
                    "foo-bar@baz.qux"
            ]
    }
}