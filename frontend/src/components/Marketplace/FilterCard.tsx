import { Button, Card, Form, InputGroup } from 'react-bootstrap';
import { strings } from '../../i18n/i18n';
import { useForm } from 'react-hook-form';
import { useListCategories } from '../../api/categories/categoriesSlice';
import { useListUsedLocations } from '../../api/locations/locationsSlice';
import { useListOrderOptions } from '../../api/orderOptions/orderOptionsSlice';
import FormInput from '../FormInputs/FormInput';
import FormSelect from '../FormInputs/FormSelect';

interface FilterCardForm {
	name: string;
	category: number;
	orderBy: string;
	location: string;
	endPrice: number;
	initPrice: number;
}

const UNSELECTED_DEFAULT = {
	id: '',
	description: strings.collection.filter.everyCategory,
	name: strings.collection.filter.everyLocation
};

interface FilterCardProps {
	onSubmit: (data: FilterCardForm) => void;
	defaultValues: FilterCardForm;
}

function FilterCard(props: FilterCardProps) {
	const { register, handleSubmit } = useForm<FilterCardForm>();
	const {
		defaultValues: { name, category, orderBy, location, initPrice, endPrice },
		onSubmit
	} = props;

	const { data: categories, isSuccess: categoriesIsSucc } = useListCategories();
	const { data: locations, isSuccess: locationsIsSucc } = useListUsedLocations();
	const { data: orderOptions, isSuccess: orderIsSucc } = useListOrderOptions();

	return (
		<>
			{categoriesIsSucc && categories && locationsIsSucc && locations && orderIsSucc && orderOptions && (
				<Card className='card-style filters-card col-md-3 col-lg-3 col-12'>
					<Card.Header className=' d-flex align-items-center '>
						<h4 className='color-rentapp-black col-9'>{strings.collection.filter.title}</h4>
					</Card.Header>

					<Form onSubmit={handleSubmit(onSubmit)}>
						<Card.Body style={{ padding: '0px' }}>
							<FormInput
								register={register}
								type='text'
								label={strings.collection.filter.name}
								value={name}
								name='name'
							/>
							<FormSelect
								register={register}
								name='category'
								label={strings.collection.filter.category}
								value={category ? category.toString() : ''}
								options={
									categories
										? [UNSELECTED_DEFAULT, ...categories].map(({ id, description }) => [
												id.toString(),
												description
										  ])
										: []
								}
							/>
							<FormSelect
								register={register}
								name='orderBy'
								label={strings.collection.filter.orderBy}
								value={orderBy}
								options={
									orderOptions ? orderOptions.map(({ id, description }) => [id, description]) : []
								}
							/>
							<FormSelect
								register={register}
								name='location'
								label={strings.collection.filter.location}
								value={location}
								options={
									locations
										? [UNSELECTED_DEFAULT, ...locations].map(({ id, name }) => [id, name])
										: []
								}
							/>
							<FormInput
								register={register}
								type='number'
								label={strings.collection.filter.minPrice}
								value={initPrice ? initPrice.toString() : ''}
								name='initPrice'
								prependIcon='$'
							/>
							<FormInput
								register={register}
								type='number'
								label={strings.collection.filter.maxPrice}
								value={endPrice ? endPrice.toString() : ''}
								name='endPrice'
								prependIcon='$'
							/>
						</Card.Body>
						<Button type='submit' className='w-100 mt-3'>
							{strings.collection.filter.button}
						</Button>
					</Form>
				</Card>
			)}
		</>
	);
}

export default FilterCard;
