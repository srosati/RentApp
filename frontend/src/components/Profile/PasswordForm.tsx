import { useEffect } from 'react';
import { Button, Col, Form, Row, Stack } from 'react-bootstrap';
import { useForm } from 'react-hook-form';
import { User } from '../../api/users/types';
import { useUpdatePassword } from '../../api/users/usersSlice';
import { strings } from '../../i18n/i18n';
import FormInput from '../FormInputs/FormInput';

interface ModifyPasswordData {
	password: string;
	confirmPassword: string;
}

export default function PasswordForm(props: { user: User; onDone: Function }) {
	const { onDone, user } = props;

	const {
		register,
		handleSubmit,
		getValues,
		formState: { errors }
	} = useForm<ModifyPasswordData>();

	const [modifyPassword, result] = useUpdatePassword();

	useEffect(() => {
		if (result.isSuccess) onDone();
	}, [result, onDone]);

	function onSubmit(data: ModifyPasswordData) {
		modifyPassword({ ...data, url: new URL('password', user.url + '/').toString() });
	}

	return (
		<Form onSubmit={handleSubmit(onSubmit)}>
			<Row xs={1} sm={2} className='g-3'>
				<Col>
					<FormInput
						register={register}
						type='password'
						name='password'
						label={strings.collection.profile.passwordForm.password}
						validation={{ required: true, minLength: 8, maxLength: 16 }}
						error={errors.password}
						errorMessage={strings.collection.login.errors.password}
					/>
				</Col>
				<Col>
					<FormInput
						register={register}
						type='password'
						name='confirmPassword'
						label={strings.collection.profile.passwordForm.confirmPassword}
						validation={{
							required: true,
							minLength: 8,
							maxLength: 16,
							validate: () => getValues('password') === getValues('confirmPassword')
						}}
						error={errors.confirmPassword}
						errorMessage={strings.collection.login.errors.password}
					/>
				</Col>
			</Row>
			<Stack direction='horizontal' className='mt-3'>
				<Button onClick={(_) => onDone()} variant='outline-danger' className='ms-auto'>
					{strings.collection.profile.passwordForm.cancel}
				</Button>
				<Button type='submit' className='ms-2'>
					{strings.collection.profile.passwordForm.save}
				</Button>
			</Stack>
		</Form>
	);
}
