import { GET_HOME_TIMELINE } from "../constants/actions";

// 另一种方式、在useMappedState时设置默认值，见'/src/pages/Home/index.js'
const initState = { home: { posts: [], page: 0} };

export default function reducer(state = initState, action) {
    const { statuses } = action.payload || {};
    const { page } = action.params || {};
    switch (action.type) {
        case GET_HOME_TIMELINE:
            return {
                ...state,
                home: {
                    posts : [...state.home.posts,  ...statuses], 
                    page,
                },
            }
        default: 
            return state;
    }
} 
