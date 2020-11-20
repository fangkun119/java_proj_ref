import { GET_COMMENTS } from "../constants/actions";

const initState = {};

export default function reducer(state = initState, action) {
    switch (action.type) {
        case GET_COMMENTS:
            const comments = action.payload || {};
            return {
                ...state,
                comments,
            }
        default: 
            return state;
    }
} 
