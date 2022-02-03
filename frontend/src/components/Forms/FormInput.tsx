import {FormGroup, FormLabel, InputGroup, FormControl} from "react-bootstrap";
import {
    FieldError,
    Path,
    UseFormRegister,
} from "react-hook-form";
import ValidationInterface from "./ValidationInterface";


export default function FormInput<T>(props: {
    register: UseFormRegister<T>;
    label: string;
    name: Path<T>;
    type: string;
    error?: FieldError | null;
    errorMessage?: string;
    placeholder?: string;
    prependIcon?: JSX.Element | string | null;
    appendIcon?: JSX.Element | null;
    validation?: Partial<ValidationInterface<T>>;
}) {
    const {
        register,
        label,
        name,
        error,
        errorMessage,
        type,
        placeholder,
        prependIcon,
        appendIcon,
        validation,
    } = props;
    return (
        <FormGroup>
            <FormLabel>{label}</FormLabel>
            <InputGroup>
                {prependIcon != null && (
                    <InputGroup.Text>{prependIcon}</InputGroup.Text>
                )}
                <FormControl
                    type={type}
                    placeholder={placeholder}
                    {...register(name, validation)}
                    isInvalid={error != null}
                />
                {appendIcon != null && <InputGroup.Text>{appendIcon}</InputGroup.Text>}
                <FormControl.Feedback type="invalid">
                    {errorMessage}
                </FormControl.Feedback>
            </InputGroup>
        </FormGroup>
    );
}