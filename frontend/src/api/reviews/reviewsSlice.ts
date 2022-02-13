import { BaseApiSlice } from '../baseApiSlice';
import { Review, ListReviewParameters, CreateReviewParameters, UpdateReviewParameters } from './types';

const ReviewsApiSlice = BaseApiSlice.injectEndpoints({
	endpoints: (build) => ({
		findReview: build.query<Review, string>({
			query: (url) => url.toString()
		}),

		listReviews: build.query<Review[], string>({
			query: (url) => url.toString()
		}),

		createReview: build.mutation<Review, CreateReviewParameters>({
			query: (args) => ({
				url: 'reviews',
				method: 'POST',
				body: args
			})
		}),

		updateReview: build.mutation<void, UpdateReviewParameters>({
			query: ({ url, ...args }) => ({
				url: url.toString(),
				method: 'PUT',
				body: args
			})
		})
	})
});

export const {
	useListReviewsQuery: useListReviews,
	useFindReviewQuery: useFindReview,
	useCreateReviewMutation: useCreateReview,
	useUpdateReviewMutation: useUpdateReview
} = ReviewsApiSlice;