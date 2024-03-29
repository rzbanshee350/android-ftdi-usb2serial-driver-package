%this code tries MFCard function
KEY = uint64(269705331270325*ones(1,8));          %not known to attacker
UID = uint32(2756948238*ones(1,8));               %known to attacker
nT  =RNG(uint32(639850976*ones(1,8)),uint32(64*ones(1,8)));	%known to attacker

nr_ks1=bitxor(uint32(round(rand(1,1)*2^32)*ones(1,8)), uint32([0 1 2 3 4 5 6 7]*32));
%nr_ks1=bitxor(uint32(ones(1,8)), uint32([0 1 2 3 4 5 6 7]*32));
suc_ks2=uint32(zeros(1,8));

[ks1, ks2, ks3, state1, state2, state3, ks_pb] = MFCard(KEY, UID, nT, nr_ks1, suc_ks2);

%test the NF20 reverse functionality
% result = NF20_REV(ks3);
% length(result)/256
% disp('do we have a correct result?')
% sum(result == state3(1))
% ks3

%try some rollback functionality
% [ks3_rb, oldstate] = LFSRRB_4(state3, uint32(zeros(1,8)), uint32(1));
% [ks2_rb, oldstate] = LFSRRB_32(oldstate, uint32(zeros(1,8)), uint32(1));
% [ks1_rb, oldstate] = LFSRRB_32(oldstate, nr_ks1, uint32(3));
% [ks0_rb, oldstate] = LFSRRB_32(oldstate, bitxor(UID,nT), uint32(1));
% oldstate == KEY

%try to crack the key
disp('Now output from GET_KEYS:')
crackedkeys = GET_KEYS(UID(1), nT(1), ks3, nr_ks1(1), suc_ks2(1), ks_pb);
disp('Num of possible keys')
length(crackedkeys)
disp('can we get keys?')
sum(crackedkeys==KEY(1))


%==========================================================================
%NOTE: it is possible that the result doesn't include the correct answer.
%because NF20_REV rely on the linear relationship assumption, which says the
%difference between states are predictable using linear relationships.
%but this only can be true in a 75% probability. 
%If it is NOT true, the NF20_REV filtering using linear relationship goes
%into the wrong way, so the result may not include the correct state.
%==========================================================================

%dec2bin(ks3)
%dec2bin(NF20(bitshift(state3,-1))*8+NF20(bitshift(state3,-2))*4+NF20(bitshift(state3,-3))*2+NF20(bitshift(state3,-4)))
%dist = uint64([0, 290845781021, 145422890510, 422721051155, 72711445255, 357995575066, 211360525577, 491804410132]);

% for i=1:8
% tmp = bitxor(result,dist(i));
% tmp1 = bitxor(state3(1),dist(i));
% ksv=NF20(bitshift(tmp,-1))*8+NF20(bitshift(tmp,-2))*4+NF20(bitshift(tmp,-3))*2+NF20(bitshift(tmp,-4));
% check(i)=sum(ksv~=ks3(i));
% 
% ksv1(i)=NF20(bitshift(tmp1,-1))*8+NF20(bitshift(tmp1,-2))*4+NF20(bitshift(tmp1,-3))*2+NF20(bitshift(tmp1,-4));
% 
% end
% disp('verify how many ksv is unexpected:')
% check
% disp('verify how many ks3 is unexpected:')
% ksv1 == ks3